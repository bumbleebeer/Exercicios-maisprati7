import java.math.BigDecimal;
import java.util.*;

public final class Carrinho {
    private final List<ItemCarrinho> itens;
    private final BigDecimal descontoCupom;

    public Carrinho() {
        this(Collections.emptyList(), BigDecimal.ZERO);
    }

    private Carrinho(List<ItemCarrinho> itens, BigDecimal descontoCupom) {
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
        this.descontoCupom = descontoCupom;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public BigDecimal getDescontoCupom() {
        return descontoCupom;
    }

    public Carrinho adicionarItem(Produto produto, int quantidade) {
        List<ItemCarrinho> novosItens = new ArrayList<>(itens);
        Optional<ItemCarrinho> itemExistente = encontrarItem(produto.getId());

        if (itemExistente.isPresent()) {
            novosItens.remove(itemExistente.get());
            novosItens.add(itemExistente.get().adicionarQuantidade(quantidade));
        } else {
            novosItens.add(new ItemCarrinho(produto, quantidade));
        }

        return new Carrinho(novosItens, descontoCupom);
    }

    public Carrinho removerItem(Long produtoId) {
        List<ItemCarrinho> novosItens = itens.stream()
                .filter(item -> !item.getProduto().getId().equals(produtoId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (novosItens.size() == itens.size()) {
            throw new IllegalArgumentException("Produto não encontrado no carrinho");
        }

        return new Carrinho(novosItens, descontoCupom);
    }

    public Carrinho aplicarCupom(BigDecimal percentualDesconto) {
        return new Carrinho(itens, percentualDesconto);
    }

    public Dinheiro calcularTotal() {
        if (itens.isEmpty()) {
            return new Dinheiro(BigDecimal.ZERO, Moeda.BRL);
        }

        Dinheiro subtotal = itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(Dinheiro::somar)
                .orElse(new Dinheiro(BigDecimal.ZERO, Moeda.BRL));

        return subtotal.aplicarDesconto(descontoCupom);
    }

    public int getTotalItens() {
        return itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
    }

    private Optional<ItemCarrinho> encontrarItem(Long produtoId) {
        return itens.stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst();
    }
}

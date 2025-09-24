import java.util.Objects;

public final class ItemCarrinho {
    private final Produto produto;
    private final int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public Dinheiro getSubtotal() {
        return produto.getPreco().multiplicar(quantidade);
    }

    public ItemCarrinho adicionarQuantidade(int quantidadeAdicional) {
        return new ItemCarrinho(produto, this.quantidade + quantidadeAdicional);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemCarrinho)) return false;
        ItemCarrinho that = (ItemCarrinho) o;
        return quantidade == that.quantidade && Objects.equals(produto.getId(), that.produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(produto.getId(), quantidade);
    }
}

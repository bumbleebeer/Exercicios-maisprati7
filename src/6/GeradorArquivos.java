import java.io.*;
import java.nio.file.*;

public class GeradorArquivos {

    public static void main(String[] args) throws Exception {
        System.out.println("Gerando arquivos Java...");

        // Criar arquivos
        criarMoeda();
        criarDinheiro();
        criarProduto();
        criarItemCarrinho();
        criarCarrinho();
        criarTestesCarrinho();

        System.out.println("Arquivos criados com sucesso!");
        System.out.println("Compilando...");

        // Compilar
        Process compile = Runtime.getRuntime().exec("javac *.java");
        compile.waitFor();

        if (compile.exitValue() == 0) {
            System.out.println("Compilação bem-sucedida!");
            System.out.println("Executando testes...");

            // Executar testes com assertions habilitadas
            Process run = Runtime.getRuntime().exec("java -ea TestesCarrinho");

            // Mostrar saída
            BufferedReader reader = new BufferedReader(new InputStreamReader(run.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Mostrar erros se houver
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(run.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            run.waitFor();
        } else {
            System.err.println("Erro na compilação!");
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(compile.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }

    private static void criarMoeda() throws IOException {
        String codigo = """
public enum Moeda {
    BRL, USD, EUR
}
""";
        Files.writeString(Paths.get("Moeda.java"), codigo);
    }

    private static void criarDinheiro() throws IOException {
        String codigo = """
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Dinheiro {
    private final BigDecimal valor;
    private final Moeda moeda;

    public Dinheiro(BigDecimal valor, Moeda moeda) {
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
        this.valor = valor.setScale(2, RoundingMode.HALF_EVEN);
        this.moeda = moeda;
    }

    public Dinheiro(String valor, Moeda moeda) {
        this(new BigDecimal(valor), moeda);
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Moeda getMoeda() {
        return moeda;
    }

    public Dinheiro somar(Dinheiro outro) {
        validarMoeda(outro);
        return new Dinheiro(this.valor.add(outro.valor), this.moeda);
    }

    public Dinheiro subtrair(Dinheiro outro) {
        validarMoeda(outro);
        return new Dinheiro(this.valor.subtract(outro.valor), this.moeda);
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(this.valor.multiply(fator), this.moeda);
    }

    public Dinheiro multiplicar(int quantidade) {
        return multiplicar(new BigDecimal(quantidade));
    }

    public Dinheiro aplicarDesconto(BigDecimal percentual) {
        if (percentual.compareTo(new BigDecimal("30")) > 0) {
            throw new IllegalArgumentException("Desconto não pode ser maior que 30%");
        }
        BigDecimal fatorDesconto = BigDecimal.ONE.subtract(percentual.divide(new BigDecimal("100"), 4, RoundingMode.HALF_EVEN));
        return new Dinheiro(this.valor.multiply(fatorDesconto), this.moeda);
    }

    private void validarMoeda(Dinheiro outro) {
        if (!this.moeda.equals(outro.moeda)) {
            throw new IllegalArgumentException("Moedas incompatíveis");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dinheiro)) return false;
        Dinheiro dinheiro = (Dinheiro) o;
        return valor.equals(dinheiro.valor) && moeda == dinheiro.moeda;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, moeda);
    }

    @Override
    public String toString() {
        return moeda + " " + valor;
    }
}
""";
        Files.writeString(Paths.get("Dinheiro.java"), codigo);
    }

    private static void criarProduto() throws IOException {
        String codigo = """
public class Produto {
    private final Long id;
    private final String nome;
    private final Dinheiro preco;

    public Produto(Long id, String nome, Dinheiro preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Dinheiro getPreco() {
        return preco;
    }
}
""";
        Files.writeString(Paths.get("Produto.java"), codigo);
    }

    private static void criarItemCarrinho() throws IOException {
        String codigo = """
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
""";
        Files.writeString(Paths.get("ItemCarrinho.java"), codigo);
    }

    private static void criarCarrinho() throws IOException {
        String codigo = """
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
""";
        Files.writeString(Paths.get("Carrinho.java"), codigo);
    }

    private static void criarTestesCarrinho() throws IOException {
        String codigo = """
import java.math.BigDecimal;

public class TestesCarrinho {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("    EXECUTANDO TESTES DO CARRINHO");
        System.out.println("=========================================");
        
        testFluxoCompleto();
        testValidacoes();
        testOperacoesImutaveis();
        testCupomDesconto();
        
        System.out.println("\\n=========================================");
        System.out.println("✅ TODOS OS TESTES PASSARAM COM SUCESSO!");
        System.out.println("=========================================");
    }

    private static void testFluxoCompleto() {
        System.out.println("\\n=== Testando Fluxo Completo ===");
        
        Produto notebook = new Produto(1L, "Notebook", new Dinheiro("2500.00", Moeda.BRL));
        Produto mouse = new Produto(2L, "Mouse", new Dinheiro("150.00", Moeda.BRL));

        Carrinho carrinho = new Carrinho();
        System.out.println("✓ Carrinho vazio criado - Itens: " + carrinho.getTotalItens());
        assert carrinho.getItens().isEmpty();
        assert carrinho.getTotalItens() == 0;

        carrinho = carrinho.adicionarItem(notebook, 1);
        System.out.println("✓ Notebook adicionado - Total itens: " + carrinho.getTotalItens() + ", Valor: " + carrinho.calcularTotal());
        assert carrinho.getTotalItens() == 1;
        assert carrinho.calcularTotal().equals(new Dinheiro("2500.00", Moeda.BRL));

        carrinho = carrinho.adicionarItem(mouse, 2);
        System.out.println("✓ 2 Mouses adicionados - Total itens: " + carrinho.getTotalItens() + ", Valor: " + carrinho.calcularTotal());
        assert carrinho.getTotalItens() == 3;
        assert carrinho.calcularTotal().equals(new Dinheiro("2800.00", Moeda.BRL));

        carrinho = carrinho.adicionarItem(notebook, 1);
        System.out.println("✓ Mais 1 Notebook adicionado - Total itens: " + carrinho.getTotalItens() + ", Valor: " + carrinho.calcularTotal());
        assert carrinho.getTotalItens() == 4;
        assert carrinho.calcularTotal().equals(new Dinheiro("5300.00", Moeda.BRL));

        carrinho = carrinho.aplicarCupom(new BigDecimal("10"));
        System.out.println("✓ Cupom 10% aplicado - Valor final: " + carrinho.calcularTotal());
        assert carrinho.calcularTotal().equals(new Dinheiro("4770.00", Moeda.BRL));

        carrinho = carrinho.removerItem(2L);
        System.out.println("✓ Mouse removido - Total itens: " + carrinho.getTotalItens() + ", Valor: " + carrinho.calcularTotal());
        assert carrinho.getTotalItens() == 2;
        assert carrinho.calcularTotal().equals(new Dinheiro("4500.00", Moeda.BRL));
    }

    private static void testValidacoes() {
        System.out.println("\\n=== Testando Validações ===");
        Produto produto = new Produto(1L, "Teste", new Dinheiro("100.00", Moeda.BRL));

        try {
            new Dinheiro("-50.00", Moeda.BRL);
            assert false : "Deveria lançar exceção para valor negativo";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Valor negativo rejeitado: " + e.getMessage());
            assert e.getMessage().contains("negativo");
        }

        try {
            new ItemCarrinho(produto, 0);
            assert false : "Deveria lançar exceção para quantidade zero";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Quantidade zero rejeitada: " + e.getMessage());
            assert e.getMessage().contains("maior que zero");
        }

        try {
            Carrinho carrinho = new Carrinho();
            carrinho.aplicarCupom(new BigDecimal("35"));
            carrinho.calcularTotal();
            assert false : "Deveria lançar exceção para cupom > 30%";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Desconto > 30% rejeitado: " + e.getMessage());
            assert e.getMessage().contains("30%");
        }

        try {
            Carrinho carrinho = new Carrinho();
            carrinho.removerItem(999L);
            assert false : "Deveria lançar exceção para produto inexistente";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Remoção de item inexistente rejeitada: " + e.getMessage());
            assert e.getMessage().contains("não encontrado");
        }
    }

    private static void testOperacoesImutaveis() {
        System.out.println("\\n=== Testando Operações Imutáveis ===");
        Produto produto = new Produto(1L, "Teste", new Dinheiro("100.00", Moeda.BRL));

        Carrinho carrinho1 = new Carrinho();
        Carrinho carrinho2 = carrinho1.adicionarItem(produto, 1);

        System.out.println("✓ Carrinho original permanece vazio: " + carrinho1.getItens().isEmpty());
        System.out.println("✓ Novo carrinho tem 1 item: " + (carrinho2.getTotalItens() == 1));
        System.out.println("✓ São objetos diferentes: " + (carrinho1 != carrinho2));
        
        assert carrinho1.getItens().isEmpty();
        assert carrinho2.getTotalItens() == 1;
        assert carrinho1 != carrinho2;

        Carrinho carrinho3 = carrinho2.aplicarCupom(new BigDecimal("5"));
        System.out.println("✓ Carrinho2 sem desconto: " + carrinho2.getDescontoCupom().equals(BigDecimal.ZERO));
        System.out.println("✓ Carrinho3 com desconto 5%: " + carrinho3.getDescontoCupom().equals(new BigDecimal("5")));
        System.out.println("✓ Carrinhos 2 e 3 são diferentes: " + (carrinho2 != carrinho3));
        
        assert carrinho2.getDescontoCupom().equals(BigDecimal.ZERO);
        assert carrinho3.getDescontoCupom().equals(new BigDecimal("5"));
        assert carrinho2 != carrinho3;
    }

    private static void testCupomDesconto() {
        System.out.println("\\n=== Testando Cupom de Desconto ===");
        Produto produto = new Produto(1L, "Teste", new Dinheiro("100.00", Moeda.BRL));

        Carrinho carrinho = new Carrinho()
                .adicionarItem(produto, 1)
                .aplicarCupom(new BigDecimal("15.5555"));

        Dinheiro total = carrinho.calcularTotal();
        System.out.println("✓ Produto R$ 100,00 com desconto 15.5555% = " + total);
        System.out.println("✓ Valor correto (R$ 84,44): " + total.equals(new Dinheiro("84.44", Moeda.BRL)));
        
        assert total.equals(new Dinheiro("84.44", Moeda.BRL));
    }
}
""";
        Files.writeString(Paths.get("TestesCarrinho.java"), codigo);
    }
}
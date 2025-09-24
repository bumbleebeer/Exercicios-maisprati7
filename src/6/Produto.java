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

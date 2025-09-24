class Produto {
    private String nome;
    private double preco;
    private int quantidadeEmEstoque;

    public Produto(String nome, double preco, int quantidadeEmEstoque) {
        setNome(nome);
        setPreco(preco);
        setQuantidadeEmEstoque(quantidadeEmEstoque);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        this.nome = nome.trim();
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        this.preco = preco;
    }

    public int getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public void setQuantidadeEmEstoque(int quantidadeEmEstoque) {
        if (quantidadeEmEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa");
        }
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    @Override
    public String toString() {
        return String.format("Produto{nome='%s', preco=%.2f, quantidadeEmEstoque=%d}",
                nome, preco, quantidadeEmEstoque);
    }
}

public class TesteProduto {
    public static void main(String[] args) {
        System.out.println("=== TESTE DA CLASSE PRODUTO ===\n");

        System.out.println("1. Criando produtos com valores válidos:");
        try {
            Produto produto1 = new Produto("Notebook", 2500.00, 10);
            Produto produto2 = new Produto("Mouse", 45.99, 50);

            System.out.println("✓ " + produto1);
            System.out.println("✓ " + produto2);
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }

        System.out.println("\n2. Alterando valores válidos:");
        try {
            Produto produto = new Produto("Teclado", 120.00, 25);
            System.out.println("Produto original: " + produto);

            produto.setNome("Teclado Mecânico");
            produto.setPreco(180.00);
            produto.setQuantidadeEmEstoque(15);

            System.out.println("✓ Produto atualizado: " + produto);
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }

        System.out.println("\n3. Testando validações para nome:");

        try {
            Produto produto = new Produto(null, 100.0, 5);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Nome nulo rejeitado: " + e.getMessage());
        }

        try {
            Produto produto = new Produto("", 100.0, 5);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Nome vazio rejeitado: " + e.getMessage());
        }

        try {
            Produto produto = new Produto("   ", 100.0, 5);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Nome só com espaços rejeitado: " + e.getMessage());
        }

        System.out.println("\n4. Testando validações para preço:");

        try {
            Produto produto = new Produto("Monitor", -500.0, 3);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Preço negativo no construtor rejeitado: " + e.getMessage());
        }

        try {
            Produto produto = new Produto("Impressora", 300.0, 2);
            produto.setPreco(-50.0);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Preço negativo no setter rejeitado: " + e.getMessage());
        }

        System.out.println("\n5. Testando validações para quantidade em estoque:");

        try {
            Produto produto = new Produto("Webcam", 150.0, -10);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Quantidade negativa no construtor rejeitada: " + e.getMessage());
        }

        try {
            Produto produto = new Produto("Headset", 200.0, 8);
            produto.setQuantidadeEmEstoque(-5);
            System.out.println("✗ Deveria ter falhado!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Quantidade negativa no setter rejeitada: " + e.getMessage());
        }

        System.out.println("\n6. Testando casos limites válidos:");
        try {
            Produto produto = new Produto("Produto Gratuito", 0.0, 0);
            System.out.println("✓ Preço zero e estoque zero aceitos: " + produto);
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro inesperado: " + e.getMessage());
        }

        System.out.println("\n7. Exemplo de uso completo:");
        try {
            Produto produto = new Produto("Smartphone", 1200.00, 30);
            System.out.println("Produto criado: " + produto);

            int vendas = 5;
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - vendas);
            System.out.println("Após vender " + vendas + " unidades: " + produto);

            int reposicao = 20;
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + reposicao);
            System.out.println("Após repor " + reposicao + " unidades: " + produto);

            double desconto = 0.10;
            double novoPreco = produto.getPreco() * (1 - desconto);
            produto.setPreco(novoPreco);
            System.out.println("Após aplicar desconto de 10%: " + produto);

        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }

        System.out.println("\n=== FIM DOS TESTES ===");
    }
}
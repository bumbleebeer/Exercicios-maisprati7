class DescontoInvalidoException extends Exception {
    public DescontoInvalidoException(String mensagem) {
        super(mensagem);
    }
}

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

    public void aplicarDesconto(double porcentagem) throws DescontoInvalidoException {
        if (porcentagem < 0 || porcentagem > 50) {
            throw new DescontoInvalidoException(
                    String.format("Desconto deve estar entre 0%% e 50%%. Valor informado: %.2f%%", porcentagem)
            );
        }

        double fatorDesconto = porcentagem / 100.0;
        double novoPreco = this.preco * (1 - fatorDesconto);
        this.preco = novoPreco;
    }

    public double calcularPrecoComDesconto(double porcentagem) throws DescontoInvalidoException {
        if (porcentagem < 0 || porcentagem > 50) {
            throw new DescontoInvalidoException(
                    String.format("Desconto deve estar entre 0%% e 50%%. Valor informado: %.2f%%", porcentagem)
            );
        }

        double fatorDesconto = porcentagem / 100.0;
        return this.preco * (1 - fatorDesconto);
    }

    @Override
    public String toString() {
        return String.format("Produto{nome='%s', preco=%.2f, quantidadeEmEstoque=%d}",
                nome, preco, quantidadeEmEstoque);
    }
}

public class TesteDescontoProduto {
    public static void main(String[] args) {
        System.out.println("=== TESTE DO MÉTODO APLICAR DESCONTO ===\n");

        Produto produto = new Produto("Smartphone Premium", 1200.00, 15);
        System.out.println("Produto criado: " + produto);

        System.out.println("\n--- TESTES COM DESCONTOS VÁLIDOS ---");

        testarDesconto(produto, 10.0, "Desconto promocional");

        testarDesconto(produto, 25.0, "Desconto black friday");

        testarDesconto(produto, 0.0, "Sem desconto");

        testarDesconto(produto, 50.0, "Desconto máximo");

        System.out.println("\n--- TESTES COM DESCONTOS INVÁLIDOS ---");

        testarDescontoInvalido(produto, -10.0, "Desconto negativo");

        testarDescontoInvalido(produto, 60.0, "Desconto acima do limite");

        testarDescontoInvalido(produto, 100.0, "Desconto de 100%");

        testarDescontoInvalido(produto, 50.01, "Desconto ligeiramente acima do limite");

        System.out.println("\n--- DEMONSTRAÇÃO PRÁTICA ---");
        demonstracaoPratica();

        System.out.println("\n--- TESTE DO MÉTODO CALCULAR PREÇO COM DESCONTO ---");
        testarCalculoDesconto();

        System.out.println("\n=== FIM DOS TESTES ===");
    }

    private static void testarDesconto(Produto produto, double porcentagem, String contexto) {
        try {
            Produto produtoTeste = new Produto(produto.getNome(), produto.getPreco(), produto.getQuantidadeEmEstoque());

            double precoOriginal = produtoTeste.getPreco();
            System.out.printf("\n%s (%.1f%%):\n", contexto, porcentagem);
            System.out.printf("  Preço antes: R$ %.2f\n", precoOriginal);

            produtoTeste.aplicarDesconto(porcentagem);

            double precoComDesconto = produtoTeste.getPreco();
            double valorDesconto = precoOriginal - precoComDesconto;

            System.out.printf("  Preço depois: R$ %.2f\n", precoComDesconto);
            System.out.printf("  Valor economizado: R$ %.2f\n", valorDesconto);
            System.out.println("  ✓ Desconto aplicado com sucesso!");

        } catch (DescontoInvalidoException e) {
            System.out.println("  ✗ Erro inesperado: " + e.getMessage());
        }
    }

    private static void testarDescontoInvalido(Produto produto, double porcentagem, String contexto) {
        try {
            Produto produtoTeste = new Produto(produto.getNome(), produto.getPreco(), produto.getQuantidadeEmEstoque());

            System.out.printf("\n%s (%.2f%%):\n", contexto, porcentagem);
            System.out.printf("  Preço original: R$ %.2f\n", produtoTeste.getPreco());

            produtoTeste.aplicarDesconto(porcentagem);
            System.out.println("  ✗ Deveria ter falhado, mas não falhou!");

        } catch (DescontoInvalidoException e) {
            System.out.println("  ✓ Desconto inválido rejeitado: " + e.getMessage());
        }
    }

    private static void demonstracaoPratica() {
        try {
            Produto[] produtos = {
                    new Produto("Notebook Gamer", 3500.00, 5),
                    new Produto("Mouse Wireless", 89.90, 25),
                    new Produto("Teclado Mecânico", 299.99, 12),
                    new Produto("Monitor 4K", 1899.00, 8)
            };

            System.out.println("Aplicando desconto de 15% em todos os produtos:\n");

            double totalOriginal = 0;
            double totalComDesconto = 0;

            for (Produto produto : produtos) {
                double precoOriginal = produto.getPreco();
                totalOriginal += precoOriginal;

                produto.aplicarDesconto(15.0);

                double precoComDesconto = produto.getPreco();
                totalComDesconto += precoComDesconto;

                System.out.printf("%-20s: R$ %8.2f → R$ %8.2f (economia: R$ %.2f)\n",
                        produto.getNome(),
                        precoOriginal,
                        precoComDesconto,
                        precoOriginal - precoComDesconto
                );
            }

            System.out.printf("\nResumo da promoção:\n");
            System.out.printf("Total original:      R$ %.2f\n", totalOriginal);
            System.out.printf("Total com desconto:  R$ %.2f\n", totalComDesconto);
            System.out.printf("Economia total:      R$ %.2f\n", totalOriginal - totalComDesconto);

        } catch (DescontoInvalidoException e) {
            System.out.println("Erro na demonstração: " + e.getMessage());
        }
    }

    private static void testarCalculoDesconto() {
        try {
            Produto produto = new Produto("Produto Teste", 100.00, 10);

            System.out.println("Simulação de descontos (sem aplicar):");
            double[] descontos = {5.0, 15.0, 30.0, 45.0};

            for (double desconto : descontos) {
                double precoComDesconto = produto.calcularPrecoComDesconto(desconto);
                System.out.printf("  %.0f%% de desconto: R$ %.2f → R$ %.2f\n",
                        desconto, produto.getPreco(), precoComDesconto);
            }

            System.out.printf("\nPreço original mantido: R$ %.2f\n", produto.getPreco());

            try {
                produto.calcularPrecoComDesconto(75.0);
                System.out.println("✗ Deveria ter falhado!");
            } catch (DescontoInvalidoException e) {
                System.out.println("✓ Cálculo com desconto inválido rejeitado: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Erro no teste de cálculo: " + e.getMessage());
        }
    }
}
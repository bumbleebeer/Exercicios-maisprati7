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

        System.out.println("\n=========================================");
        System.out.println("✅ TODOS OS TESTES PASSARAM COM SUCESSO!");
        System.out.println("=========================================");
    }

    private static void testFluxoCompleto() {
        System.out.println("\n=== Testando Fluxo Completo ===");

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
        System.out.println("\n=== Testando Validações ===");
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
        System.out.println("\n=== Testando Operações Imutáveis ===");
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
        System.out.println("\n=== Testando Cupom de Desconto ===");
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

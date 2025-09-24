import java.math.BigDecimal;
import java.util.Optional;

public class ExemploUso {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE REPOSITORIO GENERICO ===\n");

        // Criando repositórios
        IRepository<Produto, Long> produtoRepo = new InMemoryRepository<>();
        IRepository<Funcionario, String> funcionarioRepo = new InMemoryRepository<>();

        System.out.println("Criando e salvando produtos...");
        Produto produto1 = new Produto(1L, "Notebook", new BigDecimal("2500.00"));
        Produto produto2 = new Produto(2L, "Mouse", new BigDecimal("50.00"));

        produtoRepo.salvar(produto1);
        produtoRepo.salvar(produto2);
        System.out.println("Produtos salvos: " + produtoRepo.listarTodos().size());

        System.out.println("\nCriando e salvando funcionarios...");
        Funcionario func1 = new Funcionario("F001", "Joao Silva", "TI");
        Funcionario func2 = new Funcionario("F002", "Maria Santos", "RH");

        funcionarioRepo.salvar(func1);
        funcionarioRepo.salvar(func2);
        System.out.println("Funcionarios salvos: " + funcionarioRepo.listarTodos().size());

        System.out.println("\nTestando busca por ID...");
        Optional<Produto> produtoEncontrado = produtoRepo.buscarPorId(1L);
        if (produtoEncontrado.isPresent()) {
            System.out.println("Produto encontrado: " + produtoEncontrado.get());
        }

        Optional<Funcionario> funcionarioEncontrado = funcionarioRepo.buscarPorId("F001");
        if (funcionarioEncontrado.isPresent()) {
            System.out.println("Funcionario encontrado: " + funcionarioEncontrado.get());
        }

        System.out.println("\nListando todos os produtos:");
        produtoRepo.listarTodos().forEach(p -> System.out.println("  - " + p));

        System.out.println("\nListando todos os funcionarios:");
        funcionarioRepo.listarTodos().forEach(f -> System.out.println("  - " + f));

        System.out.println("\nTestando remocao com ID inexistente...");
        try {
            produtoRepo.remover(999L);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro capturado corretamente: " + e.getMessage());
        }

        System.out.println("\nRemovendo produto existente...");
        produtoRepo.remover(2L); // Remove o mouse
        System.out.println("Produto removido. Total restante: " + produtoRepo.listarTodos().size());

        System.out.println("\nProdutos apos remocao:");
        produtoRepo.listarTodos().forEach(p -> System.out.println("  - " + p));

        System.out.println("\nDemonstracao do repositorio generico concluida!");
    }
}

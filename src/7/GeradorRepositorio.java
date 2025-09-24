import java.io.*;
import java.nio.file.*;

public class GeradorRepositorio {

    public static void main(String[] args) throws Exception {
        System.out.println("Gerando arquivos do Sistema de Repositório...");

        // Criar arquivos
        criarIdentificavel();
        criarIRepository();
        criarEntidadeNaoEncontradaException();
        criarInMemoryRepository();
        criarProduto();
        criarFuncionario();
        criarExemploUso();

        System.out.println("Arquivos criados com sucesso!");
        System.out.println("Compilando...");

        // Compilar
        Process compile = Runtime.getRuntime().exec("javac *.java");
        compile.waitFor();

        if (compile.exitValue() == 0) {
            System.out.println("Compilação bem-sucedida!");
            System.out.println("Executando exemplo...");

            // Executar exemplo
            Process run = Runtime.getRuntime().exec("java ExemploUso");

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
            System.out.println("\nExecucao concluida com sucesso!");
        } else {
            System.err.println("Erro na compilação!");
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(compile.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }

    private static void criarIdentificavel() throws IOException {
        String codigo = """
public interface Identificavel<ID> {
    ID getId();
}
""";
        Files.writeString(Paths.get("Identificavel.java"), codigo);
    }

    private static void criarIRepository() throws IOException {
        String codigo = """
import java.util.List;
import java.util.Optional;

public interface IRepository<T extends Identificavel<ID>, ID> {
    void salvar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void remover(ID id);
}
""";
        Files.writeString(Paths.get("IRepository.java"), codigo);
    }

    private static void criarEntidadeNaoEncontradaException() throws IOException {
        String codigo = """
public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
""";
        Files.writeString(Paths.get("EntidadeNaoEncontradaException.java"), codigo);
    }

    private static void criarInMemoryRepository() throws IOException {
        String codigo = """
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository<T extends Identificavel<ID>, ID> implements IRepository<T, ID> {
    private final Map<ID, T> dados = new ConcurrentHashMap<>();

    @Override
    public void salvar(T entidade) {
        dados.put(entidade.getId(), entidade);
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void remover(ID id) {
        if (!dados.containsKey(id)) {
            throw new EntidadeNaoEncontradaException("Entidade com ID " + id + " não encontrada");
        }
        dados.remove(id);
    }
}
""";
        Files.writeString(Paths.get("InMemoryRepository.java"), codigo);
    }

    private static void criarProduto() throws IOException {
        String codigo = """
import java.math.BigDecimal;

public class Produto implements Identificavel<Long> {
    private Long id;
    private String nome;
    private BigDecimal preco;

    public Produto(Long id, String nome, BigDecimal preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
    
    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', preco=" + preco + "}";
    }
}
""";
        Files.writeString(Paths.get("Produto.java"), codigo);
    }

    private static void criarFuncionario() throws IOException {
        String codigo = """
public class Funcionario implements Identificavel<String> {
    private String id;
    private String nome;
    private String departamento;

    public Funcionario(String id, String nome, String departamento) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    @Override
    public String toString() {
        return "Funcionario{id='" + id + "', nome='" + nome + "', departamento='" + departamento + "'}";
    }
}
""";
        Files.writeString(Paths.get("Funcionario.java"), codigo);
    }

    private static void criarExemploUso() throws IOException {
        String codigo = """
import java.math.BigDecimal;
import java.util.Optional;

public class ExemploUso {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE REPOSITORIO GENERICO ===\\n");
        
        // Criando repositórios
        IRepository<Produto, Long> produtoRepo = new InMemoryRepository<>();
        IRepository<Funcionario, String> funcionarioRepo = new InMemoryRepository<>();

        System.out.println("Criando e salvando produtos...");
        Produto produto1 = new Produto(1L, "Notebook", new BigDecimal("2500.00"));
        Produto produto2 = new Produto(2L, "Mouse", new BigDecimal("50.00"));

        produtoRepo.salvar(produto1);
        produtoRepo.salvar(produto2);
        System.out.println("Produtos salvos: " + produtoRepo.listarTodos().size());

        System.out.println("\\nCriando e salvando funcionarios...");
        Funcionario func1 = new Funcionario("F001", "Joao Silva", "TI");
        Funcionario func2 = new Funcionario("F002", "Maria Santos", "RH");

        funcionarioRepo.salvar(func1);
        funcionarioRepo.salvar(func2);
        System.out.println("Funcionarios salvos: " + funcionarioRepo.listarTodos().size());

        System.out.println("\\nTestando busca por ID...");
        Optional<Produto> produtoEncontrado = produtoRepo.buscarPorId(1L);
        if (produtoEncontrado.isPresent()) {
            System.out.println("Produto encontrado: " + produtoEncontrado.get());
        }
        
        Optional<Funcionario> funcionarioEncontrado = funcionarioRepo.buscarPorId("F001");
        if (funcionarioEncontrado.isPresent()) {
            System.out.println("Funcionario encontrado: " + funcionarioEncontrado.get());
        }

        System.out.println("\\nListando todos os produtos:");
        produtoRepo.listarTodos().forEach(p -> System.out.println("  - " + p));
        
        System.out.println("\\nListando todos os funcionarios:");
        funcionarioRepo.listarTodos().forEach(f -> System.out.println("  - " + f));

        System.out.println("\\nTestando remocao com ID inexistente...");
        try {
            produtoRepo.remover(999L);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro capturado corretamente: " + e.getMessage());
        }
        
        System.out.println("\\nRemovendo produto existente...");
        produtoRepo.remover(2L); // Remove o mouse
        System.out.println("Produto removido. Total restante: " + produtoRepo.listarTodos().size());
        
        System.out.println("\\nProdutos apos remocao:");
        produtoRepo.listarTodos().forEach(p -> System.out.println("  - " + p));
        
        System.out.println("\\nDemonstracao do repositorio generico concluida!");
    }
}
""";
        Files.writeString(Paths.get("ExemploUso.java"), codigo);
    }
}
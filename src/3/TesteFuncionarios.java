import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


abstract class Funcionario {
    protected String nome;
    protected BigDecimal salario;

    public Funcionario(String nome, BigDecimal salario) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (salario == null || salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salário deve ser positivo");
        }

        this.nome = nome.trim();
        this.salario = salario;
    }

    public Funcionario(String nome, double salario) {
        this(nome, BigDecimal.valueOf(salario));
    }


    public String getNome() {
        return nome;
    }

    public BigDecimal getSalario() {
        return salario;
    }


    public abstract BigDecimal calcularBonus();


    public String getTipo() {
        return this.getClass().getSimpleName();
    }


    @Override
    public String toString() {
        return String.format("%s: %s - Salário: R$ %s",
                getTipo(), nome, salario.setScale(2, RoundingMode.HALF_UP));
    }
}


class Gerente extends Funcionario {
    private static final BigDecimal PERCENTUAL_BONUS = new BigDecimal("0.20");

    public Gerente(String nome, BigDecimal salario) {
        super(nome, salario);
    }

    public Gerente(String nome, double salario) {
        super(nome, salario);
    }

    @Override
    public BigDecimal calcularBonus() {
        return salario.multiply(PERCENTUAL_BONUS).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Bônus: R$ %s (20%%)",
                calcularBonus().setScale(2, RoundingMode.HALF_UP));
    }
}


class Desenvolvedor extends Funcionario {
    private static final BigDecimal PERCENTUAL_BONUS = new BigDecimal("0.10"); // 10%

    public Desenvolvedor(String nome, BigDecimal salario) {
        super(nome, salario);
    }

    public Desenvolvedor(String nome, double salario) {
        super(nome, salario);
    }

    @Override
    public BigDecimal calcularBonus() {
        return salario.multiply(PERCENTUAL_BONUS).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Bônus: R$ %s (10%%)",
                calcularBonus().setScale(2, RoundingMode.HALF_UP));
    }
}


class Analista extends Funcionario {
    private static final BigDecimal PERCENTUAL_BONUS = new BigDecimal("0.15");

    public Analista(String nome, BigDecimal salario) {
        super(nome, salario);
    }

    public Analista(String nome, double salario) {
        super(nome, salario);
    }

    @Override
    public BigDecimal calcularBonus() {
        return salario.multiply(PERCENTUAL_BONUS).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Bônus: R$ %s (15%%)",
                calcularBonus().setScale(2, RoundingMode.HALF_UP));
    }
}


public class TesteFuncionarios {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE FUNCIONÁRIOS E BÔNUS ===\n");

        List<Funcionario> funcionarios = new ArrayList<>();

        try {
            funcionarios.add(new Gerente("Maria Silva", new BigDecimal("8000.00")));
            funcionarios.add(new Gerente("João Santos", 12000.50));

            funcionarios.add(new Desenvolvedor("Ana Costa", new BigDecimal("5500.00")));
            funcionarios.add(new Desenvolvedor("Pedro Oliveira", 6800.75));
            funcionarios.add(new Desenvolvedor("Carlos Lima", 4200.00));

            funcionarios.add(new Analista("Lucia Ferreira", new BigDecimal("4800.00")));
            funcionarios.add(new Analista("Rafael Souza", 5200.25));

            System.out.println("1. Lista de funcionários criada com sucesso!");
            System.out.println("Total de funcionários: " + funcionarios.size() + "\n");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar funcionário: " + e.getMessage());
        }

        System.out.println("2. INFORMAÇÕES DOS FUNCIONÁRIOS E BÔNUS:");
        System.out.println("=" .repeat(70));

        BigDecimal totalSalarios = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;

        for (int i = 0; i < funcionarios.size(); i++) {
            Funcionario funcionario = funcionarios.get(i);
            BigDecimal bonus = funcionario.calcularBonus();

            System.out.printf("%d. %s\n", i + 1, funcionario);

            totalSalarios = totalSalarios.add(funcionario.getSalario());
            totalBonus = totalBonus.add(bonus);
        }

        System.out.println("\n" + "=" .repeat(70));
        System.out.println("3. RESUMO FINANCEIRO:");
        System.out.printf("Total em salários: R$ %s\n",
                totalSalarios.setScale(2, RoundingMode.HALF_UP));
        System.out.printf("Total em bônus:    R$ %s\n",
                totalBonus.setScale(2, RoundingMode.HALF_UP));
        System.out.printf("Custo total:       R$ %s\n",
                totalSalarios.add(totalBonus).setScale(2, RoundingMode.HALF_UP));

        System.out.println("\n" + "=" .repeat(70));
        System.out.println("4. ANÁLISE POR CATEGORIA:");

        analisarPorCategoria(funcionarios, Gerente.class, "GERENTES");
        analisarPorCategoria(funcionarios, Desenvolvedor.class, "DESENVOLVEDORES");
        analisarPorCategoria(funcionarios, Analista.class, "ANALISTAS");

        System.out.println("\n" + "=" .repeat(70));
        System.out.println("5. TESTES DE VALIDAÇÃO:");

        testarValidacoes();

        System.out.println("\n" + "=" .repeat(70));
        System.out.println("6. DEMONSTRAÇÃO DE FUNCIONALIDADES:");

        demonstrarFuncionalidades(funcionarios);

        System.out.println("\n=== FIM DA DEMONSTRAÇÃO ===");
    }

    private static void analisarPorCategoria(List<Funcionario> funcionarios,
                                             Class<?> tipo, String nomeCategoria) {
        System.out.println("\n" + nomeCategoria + ":");

        List<Funcionario> categoria = new ArrayList<>();
        for (Funcionario funcionario : funcionarios) {
            if (tipo.isInstance(funcionario)) {
                categoria.add(funcionario);
            }
        }

        if (categoria.isEmpty()) {
            System.out.println("  Nenhum funcionário nesta categoria.");
            return;
        }

        BigDecimal totalSalario = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;

        for (Funcionario funcionario : categoria) {
            totalSalario = totalSalario.add(funcionario.getSalario());
            totalBonus = totalBonus.add(funcionario.calcularBonus());
            System.out.printf("  • %s - Salário: R$ %s - Bônus: R$ %s\n",
                    funcionario.getNome(),
                    funcionario.getSalario().setScale(2, RoundingMode.HALF_UP),
                    funcionario.calcularBonus().setScale(2, RoundingMode.HALF_UP));
        }

        System.out.printf("  └─ Total: %d funcionários | Salários: R$ %s | Bônus: R$ %s\n",
                categoria.size(),
                totalSalario.setScale(2, RoundingMode.HALF_UP),
                totalBonus.setScale(2, RoundingMode.HALF_UP));
    }

    private static void testarValidacoes() {
        try {
            new Gerente(null, 5000.00);
            System.out.println("✗ Deveria ter falhado com nome nulo!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Nome nulo rejeitado: " + e.getMessage());
        }

        try {
            new Desenvolvedor("", 4000.00);
            System.out.println("✗ Deveria ter falhado com nome vazio!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Nome vazio rejeitado: " + e.getMessage());
        }

        try {
            new Analista("Teste", BigDecimal.ZERO);
            System.out.println("✗ Deveria ter falhado com salário zero!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Salário zero rejeitado: " + e.getMessage());
        }

        try {
            new Gerente("Teste", -1000.00);
            System.out.println("✗ Deveria ter falhado com salário negativo!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Salário negativo rejeitado: " + e.getMessage());
        }

        try {
            new Desenvolvedor("Teste", (BigDecimal) null);
            System.out.println("✗ Deveria ter falhado com salário nulo!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Salário nulo rejeitado: " + e.getMessage());
        }
    }

    private static void demonstrarFuncionalidades(List<Funcionario> funcionarios) {
        if (funcionarios.isEmpty()) return;

        Funcionario funcionario = funcionarios.get(0);

        System.out.println("Exemplo com: " + funcionario.getNome());
        System.out.println("├─ Tipo: " + funcionario.getTipo());
        System.out.println("├─ Salário: R$ " + funcionario.getSalario().setScale(2, RoundingMode.HALF_UP));
        System.out.println("├─ Bônus: R$ " + funcionario.calcularBonus().setScale(2, RoundingMode.HALF_UP));
        System.out.println("└─ Salário + Bônus: R$ " +
                funcionario.getSalario().add(funcionario.calcularBonus()).setScale(2, RoundingMode.HALF_UP));

        System.out.println("\nPolimorfismo em ação:");
        for (int i = 0; i < Math.min(3, funcionarios.size()); i++) {
            Funcionario f = funcionarios.get(i);
            System.out.printf("• %s (%s): Bônus = R$ %s\n",
                    f.getNome(),
                    f.getTipo(),
                    f.calcularBonus().setScale(2, RoundingMode.HALF_UP));
        }
    }
}
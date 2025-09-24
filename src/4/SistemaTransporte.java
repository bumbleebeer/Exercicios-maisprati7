import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class VelocidadeInvalidaException extends Exception {
    public VelocidadeInvalidaException(String mensagem) {
        super(mensagem);
    }
}

class OperacaoInvalidaException extends Exception {
    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}

interface IMeioTransporte {
    void acelerar() throws VelocidadeInvalidaException, OperacaoInvalidaException;
    void frear() throws VelocidadeInvalidaException, OperacaoInvalidaException;
    double getVelocidadeAtual();
    double getVelocidadeMaxima();
    String getTipo();
    String getStatus();
}

class Carro implements IMeioTransporte {
    private double velocidadeAtual;
    private final double velocidadeMaxima = 180.0;
    private final double incrementoAceleracao = 15.0;
    private final double decrementoFreada = 20.0;
    private boolean ligado;
    private String modelo;

    public Carro(String modelo) {
        this.modelo = modelo;
        this.velocidadeAtual = 0.0;
        this.ligado = false;
    }

    public void ligar() {
        this.ligado = true;
    }

    public void desligar() throws OperacaoInvalidaException {
        if (velocidadeAtual > 0) {
            throw new OperacaoInvalidaException("Não é possível desligar o carro em movimento");
        }
        this.ligado = false;
    }

    @Override
    public void acelerar() throws VelocidadeInvalidaException, OperacaoInvalidaException {
        if (!ligado) {
            throw new OperacaoInvalidaException("Carro deve estar ligado para acelerar");
        }

        double novaVelocidade = velocidadeAtual + incrementoAceleracao;

        if (novaVelocidade > velocidadeMaxima) {
            throw new VelocidadeInvalidaException(
                    String.format("Velocidade máxima atingida! Máximo: %.1f km/h", velocidadeMaxima)
            );
        }

        velocidadeAtual = novaVelocidade;
    }

    @Override
    public void frear() throws VelocidadeInvalidaException, OperacaoInvalidaException {
        if (!ligado) {
            throw new OperacaoInvalidaException("Carro deve estar ligado para frear");
        }

        double novaVelocidade = Math.max(0, velocidadeAtual - decrementoFreada);
        velocidadeAtual = novaVelocidade;
    }

    @Override
    public double getVelocidadeAtual() {
        return velocidadeAtual;
    }

    @Override
    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    @Override
    public String getTipo() {
        return "Carro";
    }

    @Override
    public String getStatus() {
        return String.format("%s (%s) - %s - %.1f/%.1f km/h",
                getTipo(), modelo, ligado ? "Ligado" : "Desligado",
                velocidadeAtual, velocidadeMaxima);
    }
}

class Bicicleta implements IMeioTransporte {
    private double velocidadeAtual;
    private final double velocidadeMaxima = 45.0;
    private final double incrementoAceleracao = 5.0;
    private final double decrementoFreada = 8.0;
    private int energia;
    private String tipo;

    public Bicicleta(String tipo) {
        this.tipo = tipo;
        this.velocidadeAtual = 0.0;
        this.energia = 100;
    }

    @Override
    public void acelerar() throws VelocidadeInvalidaException, OperacaoInvalidaException {
        if (energia <= 0) {
            throw new OperacaoInvalidaException("Ciclista sem energia para pedalar");
        }

        double incremento = incrementoAceleracao * (energia / 100.0);
        double novaVelocidade = velocidadeAtual + incremento;

        if (novaVelocidade > velocidadeMaxima) {
            throw new VelocidadeInvalidaException(
                    String.format("Velocidade máxima atingida! Máximo: %.1f km/h", velocidadeMaxima)
            );
        }

        velocidadeAtual = novaVelocidade;
        energia = Math.max(0, energia - 5);
    }

    @Override
    public void frear() throws VelocidadeInvalidaException, OperacaoInvalidaException {
        double novaVelocidade = Math.max(0, velocidadeAtual - decrementoFreada);
        velocidadeAtual = novaVelocidade;

        energia = Math.min(100, energia + 2);
    }

    public void descansar() {
        energia = Math.min(100, energia + 20);
        velocidadeAtual = Math.max(0, velocidadeAtual - 10);
    }

    @Override
    public double getVelocidadeAtual() {
        return velocidadeAtual;
    }

    @Override
    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    @Override
    public String getTipo() {
        return "Bicicleta";
    }

    @Override
    public String getStatus() {
        return String.format("%s (%s) - Energia: %d%% - %.1f/%.1f km/h",
                getTipo(), tipo, energia, velocidadeAtual, velocidadeMaxima);
    }
}

class Trem implements IMeioTransporte {
    private double velocidadeAtual;
    private final double velocidadeMaxima = 300.0; // km/h (trem de alta velocidade)
    private final double incrementoAceleracao = 25.0; // km/h por aceleração
    private final double decrementoFreada = 30.0; // km/h por freada
    private boolean sistemaOperacional;
    private int numeroVagoes;
    private String linha;

    public Trem(String linha, int numeroVagoes) {
        this.linha = linha;
        this.numeroVagoes = numeroVagoes;
        this.velocidadeAtual = 0.0;
        this.sistemaOperacional = true;
    }

    public void ativarSistemaSeguranca() {
        this.sistemaOperacional = true;
    }

    public void desativarSistemaSeguranca() throws OperacaoInvalidaException {
        if (velocidadeAtual > 0) {
            throw new OperacaoInvalidaException("Sistema de segurança não pode ser desativado em movimento");
        }
        this.sistemaOperacional = false;
    }

    @Override
    public void acelerar() throws VelocidadeInvalidaException, OperacaoInvalidaException {
        if (!sistemaOperacional) {
            throw new OperacaoInvalidaException("Sistema operacional deve estar ativo para acelerar");
        }

        double fatorPeso = Math.max(0.5, 1.0 - (numeroVagoes * 0.05));
        double incremento = incrementoAceleracao * fatorPeso;
        double novaVelocidade = velocidadeAtual + incremento;

        if (novaVelocidade > velocidadeMaxima) {
            throw new VelocidadeInvalidaException(
                    String.format("Velocidade máxima atingida! Máximo: %.1f km/h", velocidadeMaxima)
            );
        }

        velocidadeAtual = novaVelocidade;
    }

    @Override
    public void frear() throws VelocidadeInvalidaException, OperacaoInvalidaException {
        if (!sistemaOperacional) {
            throw new OperacaoInvalidaException("Sistema operacional deve estar ativo para frear");
        }

        double fatorPeso = Math.max(0.7, 1.0 - (numeroVagoes * 0.03));
        double decremento = decrementoFreada * fatorPeso;
        double novaVelocidade = Math.max(0, velocidadeAtual - decremento);

        velocidadeAtual = novaVelocidade;
    }

    public void freioEmergencia() {
        velocidadeAtual = Math.max(0, velocidadeAtual - (decrementoFreada * 2));
    }

    @Override
    public double getVelocidadeAtual() {
        return velocidadeAtual;
    }

    @Override
    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    @Override
    public String getTipo() {
        return "Trem";
    }

    @Override
    public String getStatus() {
        return String.format("%s (Linha %s, %d vagões) - %s - %.1f/%.1f km/h",
                getTipo(), linha, numeroVagoes,
                sistemaOperacional ? "Operacional" : "Fora de serviço",
                velocidadeAtual, velocidadeMaxima);
    }
}

public class SistemaTransporte {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE MEIOS DE TRANSPORTE ===\n");

        List<IMeioTransporte> transportes = new ArrayList<>();

        Carro carro1 = new Carro("Honda Civic");
        carro1.ligar();
        transportes.add(carro1);

        transportes.add(new Carro("Toyota Corolla")); // Carro desligado
        transportes.add(new Bicicleta("Mountain Bike"));
        transportes.add(new Bicicleta("Speed"));
        transportes.add(new Trem("Linha Azul", 8));
        transportes.add(new Trem("Linha Vermelha", 12));

        System.out.println("1. ESTADO INICIAL DOS TRANSPORTES:");
        System.out.println("=" .repeat(80));
        exibirStatusTransportes(transportes);

        System.out.println("\n2. DEMONSTRAÇÃO DE ACELERAÇÃO:");
        System.out.println("=" .repeat(80));
        demonstrarAceleracao(transportes);

        System.out.println("\n3. DEMONSTRAÇÃO DE FRENAGEM:");
        System.out.println("=" .repeat(80));
        demonstrarFrenagem(transportes);

        System.out.println("\n4. TESTES DE CASOS LIMITE E EXCEÇÕES:");
        System.out.println("=" .repeat(80));
        testarCasosLimite(transportes);

        System.out.println("\n5. SIMULAÇÃO DE VIAGEM:");
        System.out.println("=" .repeat(80));
        simularViagem(transportes);

        System.out.println("\n=== FIM DA DEMONSTRAÇÃO ===");
    }

    private static void exibirStatusTransportes(List<IMeioTransporte> transportes) {
        for (int i = 0; i < transportes.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, transportes.get(i).getStatus());
        }
    }

    private static void demonstrarAceleracao(List<IMeioTransporte> transportes) {
        System.out.println("Tentando acelerar todos os transportes...\n");

        for (IMeioTransporte transporte : transportes) {
            try {
                System.out.printf("🚀 Acelerando %s...\n", transporte.getTipo());
                double velocidadeAntes = transporte.getVelocidadeAtual();

                transporte.acelerar();

                double velocidadeDepois = transporte.getVelocidadeAtual();
                System.out.printf("   ✅ %.1f → %.1f km/h (+%.1f km/h)\n",
                        velocidadeAntes, velocidadeDepois, velocidadeDepois - velocidadeAntes);

            } catch (VelocidadeInvalidaException e) {
                System.out.printf("   ⚠️  %s\n", e.getMessage());
            } catch (OperacaoInvalidaException e) {
                System.out.printf("   ❌ %s\n", e.getMessage());
            }
            System.out.printf("   Status: %s\n\n", transporte.getStatus());
        }
    }

    private static void demonstrarFrenagem(List<IMeioTransporte> transportes) {
        System.out.println("Tentando frear todos os transportes...\n");

        for (IMeioTransporte transporte : transportes) {
            try {
                System.out.printf("🛑 Freando %s...\n", transporte.getTipo());
                double velocidadeAntes = transporte.getVelocidadeAtual();

                transporte.frear();

                double velocidadeDepois = transporte.getVelocidadeAtual();
                System.out.printf("   ✅ %.1f → %.1f km/h (-%.1f km/h)\n",
                        velocidadeAntes, velocidadeDepois, velocidadeAntes - velocidadeDepois);

            } catch (VelocidadeInvalidaException e) {
                System.out.printf("   ⚠️  %s\n", e.getMessage());
            } catch (OperacaoInvalidaException e) {
                System.out.printf("   ❌ %s\n", e.getMessage());
            }
            System.out.printf("   Status: %s\n\n", transporte.getStatus());
        }
    }

    private static void testarCasosLimite(List<IMeioTransporte> transportes) {

        IMeioTransporte bicicleta = transportes.stream()
                .filter(t -> t instanceof Bicicleta)
                .findFirst().orElse(null);

        if (bicicleta != null) {
            System.out.println("Testando aceleração até o limite (Bicicleta):");

            int tentativas = 0;
            while (tentativas < 15) {
                try {
                    bicicleta.acelerar();
                    System.out.printf("  Tentativa %d: %.1f km/h\n",
                            tentativas + 1, bicicleta.getVelocidadeAtual());
                    tentativas++;
                } catch (VelocidadeInvalidaException e) {
                    System.out.printf("  ⚠️  Limite atingido: %s\n", e.getMessage());
                    break;
                } catch (OperacaoInvalidaException e) {
                    System.out.printf("  ❌ Operação inválida: %s\n", e.getMessage());
                    break;
                }
            }
        }

        System.out.println("\nTestando operações com carro desligado:");
        Carro carroDesligado = new Carro("Carro Teste");

        try {
            carroDesligado.acelerar();
        } catch (Exception e) {
            System.out.printf("  ✅ Aceleração bloqueada: %s\n", e.getMessage());
        }

        try {
            carroDesligado.frear();
        } catch (Exception e) {
            System.out.printf("  ✅ Frenagem bloqueada: %s\n", e.getMessage());
        }
    }

    private static void simularViagem(List<IMeioTransporte> transportes) {
        System.out.println("Simulando uma viagem de 5 minutos para cada transporte:\n");

        Random random = new Random();

        for (IMeioTransporte transporte : transportes) {
            System.out.printf("🛣️  Viagem com %s:\n", transporte.getTipo());
            System.out.printf("   Estado inicial: %s\n", transporte.getStatus());

            if (transporte instanceof Carro) {
                ((Carro) transporte).ligar();
            }

            for (int i = 0; i < 5; i++) {
                try {
                    if (random.nextBoolean()) {
                        transporte.acelerar();
                        System.out.printf("   Minuto %d: Acelerou para %.1f km/h\n",
                                i + 1, transporte.getVelocidadeAtual());
                    } else {
                        transporte.frear();
                        System.out.printf("   Minuto %d: Freou para %.1f km/h\n",
                                i + 1, transporte.getVelocidadeAtual());
                    }
                } catch (VelocidadeInvalidaException e) {
                    System.out.printf("   Minuto %d: ⚠️  %s\n", i + 1, e.getMessage());
                } catch (OperacaoInvalidaException e) {
                    System.out.printf("   Minuto %d: ❌ %s\n", i + 1, e.getMessage());
                }

                if (transporte instanceof Bicicleta && random.nextInt(10) < 2) {
                    ((Bicicleta) transporte).descansar();
                    System.out.printf("   Minuto %d: 😴 Ciclista descansou\n", i + 1);
                }
            }

            System.out.printf("   Estado final: %s\n\n", transporte.getStatus());
        }

        System.out.println("RESUMO FINAL DAS VELOCIDADES:");
        System.out.println("-" .repeat(50));
        for (int i = 0; i < transportes.size(); i++) {
            IMeioTransporte t = transportes.get(i);
            double porcentagem = (t.getVelocidadeAtual() / t.getVelocidadeMaxima()) * 100;
            System.out.printf("%d. %s: %.1f km/h (%.1f%% da velocidade máxima)\n",
                    i + 1, t.getTipo(), t.getVelocidadeAtual(), porcentagem);
        }
    }
}
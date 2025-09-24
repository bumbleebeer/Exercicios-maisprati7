import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

enum Moeda {
    BRL, USD, EUR
}

final class Dinheiro {
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

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(this.valor.multiply(fator), this.moeda);
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

class Produto {
    private final Long id;
    private final String nome;
    private final Dinheiro preco;
    private final BigDecimal peso;

    public Produto(Long id, String nome, Dinheiro preco, BigDecimal peso) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.peso = peso;
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

    public BigDecimal getPeso() {
        return peso;
    }
}

final class ItemCarrinho {
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
        return produto.getPreco().multiplicar(new BigDecimal(quantidade));
    }

    public BigDecimal getPesoTotal() {
        return produto.getPeso().multiply(new BigDecimal(quantidade));
    }
}

enum Regiao {
    SUDESTE, SUL, NORDESTE, NORTE, CENTRO_OESTE
}

class CEP {
    private final String codigo;
    private final Regiao regiao;
    private static final Pattern CEP_PATTERN = Pattern.compile("\\d{5}-?\\d{3}");
    private static final Map<String, Regiao> REGIOES_CEP = new HashMap<>();

    static {
        REGIOES_CEP.put("01", Regiao.SUDESTE);
        REGIOES_CEP.put("02", Regiao.SUDESTE);
        REGIOES_CEP.put("03", Regiao.SUDESTE);
        REGIOES_CEP.put("04", Regiao.SUDESTE);
        REGIOES_CEP.put("05", Regiao.SUDESTE);
        REGIOES_CEP.put("20", Regiao.SUDESTE);
        REGIOES_CEP.put("21", Regiao.SUDESTE);
        REGIOES_CEP.put("22", Regiao.SUDESTE);
        REGIOES_CEP.put("23", Regiao.SUDESTE);
        REGIOES_CEP.put("24", Regiao.SUDESTE);
        REGIOES_CEP.put("80", Regiao.SUL);
        REGIOES_CEP.put("81", Regiao.SUL);
        REGIOES_CEP.put("82", Regiao.SUL);
        REGIOES_CEP.put("90", Regiao.SUL);
        REGIOES_CEP.put("91", Regiao.SUL);
        REGIOES_CEP.put("40", Regiao.NORDESTE);
        REGIOES_CEP.put("41", Regiao.NORDESTE);
        REGIOES_CEP.put("50", Regiao.NORDESTE);
        REGIOES_CEP.put("51", Regiao.NORDESTE);
        REGIOES_CEP.put("60", Regiao.NORDESTE);
        REGIOES_CEP.put("69", Regiao.NORTE);
        REGIOES_CEP.put("68", Regiao.NORTE);
        REGIOES_CEP.put("70", Regiao.CENTRO_OESTE);
        REGIOES_CEP.put("71", Regiao.CENTRO_OESTE);
        REGIOES_CEP.put("72", Regiao.CENTRO_OESTE);
        REGIOES_CEP.put("78", Regiao.CENTRO_OESTE);
    }

    public CEP(String codigo) {
        if (codigo == null || !CEP_PATTERN.matcher(codigo.replace("-", "")).matches()) {
            throw new IllegalArgumentException("CEP inválido: " + codigo);
        }

        this.codigo = codigo.replace("-", "");
        String prefixo = this.codigo.substring(0, 2);
        this.regiao = REGIOES_CEP.get(prefixo);

        if (this.regiao == null) {
            throw new IllegalArgumentException("CEP não mapeado para região: " + codigo);
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public Regiao getRegiao() {
        return regiao;
    }
}

@FunctionalInterface
interface CalculadoraFrete {
    BigDecimal calcular(Pedido pedido);
}

class SedexStrategy implements CalculadoraFrete {
    private static final Map<Regiao, BigDecimal> VALORES_BASE = Map.of(
            Regiao.SUDESTE, new BigDecimal("15.00"),
            Regiao.SUL, new BigDecimal("20.00"),
            Regiao.NORDESTE, new BigDecimal("25.00"),
            Regiao.NORTE, new BigDecimal("35.00"),
            Regiao.CENTRO_OESTE, new BigDecimal("30.00")
    );

    @Override
    public BigDecimal calcular(Pedido pedido) {
        BigDecimal valorBase = VALORES_BASE.get(pedido.getCep().getRegiao());
        BigDecimal pesoTotal = pedido.getPesoTotal();
        BigDecimal adicionalPeso = pesoTotal.multiply(new BigDecimal("2.50"));
        return valorBase.add(adicionalPeso).setScale(2, RoundingMode.HALF_EVEN);
    }
}

class PacStrategy implements CalculadoraFrete {
    private static final Map<Regiao, BigDecimal> VALORES_BASE = Map.of(
            Regiao.SUDESTE, new BigDecimal("8.00"),
            Regiao.SUL, new BigDecimal("12.00"),
            Regiao.NORDESTE, new BigDecimal("15.00"),
            Regiao.NORTE, new BigDecimal("25.00"),
            Regiao.CENTRO_OESTE, new BigDecimal("18.00")
    );

    @Override
    public BigDecimal calcular(Pedido pedido) {
        BigDecimal valorBase = VALORES_BASE.get(pedido.getCep().getRegiao());
        BigDecimal pesoTotal = pedido.getPesoTotal();
        BigDecimal adicionalPeso = pesoTotal.multiply(new BigDecimal("1.50"));
        return valorBase.add(adicionalPeso).setScale(2, RoundingMode.HALF_EVEN);
    }
}

class RetiradaNaLojaStrategy implements CalculadoraFrete {
    @Override
    public BigDecimal calcular(Pedido pedido) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    }
}

class Pedido {
    private final List<ItemCarrinho> itens;
    private final CEP cep;
    private CalculadoraFrete estrategiaFrete;

    public Pedido(List<ItemCarrinho> itens, CEP cep, CalculadoraFrete estrategiaFrete) {
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
        this.cep = cep;
        this.estrategiaFrete = estrategiaFrete;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public CEP getCep() {
        return cep;
    }

    public void setEstrategiaFrete(CalculadoraFrete estrategiaFrete) {
        this.estrategiaFrete = estrategiaFrete;
    }

    public BigDecimal calcularFrete() {
        return estrategiaFrete.calcular(this);
    }

    public Dinheiro calcularSubtotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(Dinheiro::somar)
                .orElse(new Dinheiro(BigDecimal.ZERO, Moeda.BRL));
    }

    public Dinheiro calcularTotal() {
        Dinheiro subtotal = calcularSubtotal();
        Dinheiro frete = new Dinheiro(calcularFrete(), subtotal.getMoeda());
        return subtotal.somar(frete);
    }

    public BigDecimal getPesoTotal() {
        return itens.stream()
                .map(ItemCarrinho::getPesoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

public class TesteFreteCompleto {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTES DE FRETE ===\n");

        testValidacaoCEP();
        testEstrategiasFrete();
        testTrocaEstrategiaRuntime();
        testFretePromocional();

        System.out.println("\n=== TODOS OS TESTES PASSARAM! ===");
    }

    private static void testValidacaoCEP() {
        System.out.println("1. Testando validação de CEP...");

        try {
            new CEP("12345-678");
            assert false : "Deveria lançar exceção para CEP não mapeado";
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ CEP não mapeado rejeitado: " + e.getMessage());
        }

        try {
            new CEP("123456789");
            assert false : "Deveria lançar exceção para CEP inválido";
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ CEP inválido rejeitado: " + e.getMessage());
        }

        CEP cep = new CEP("01310-100");
        assert cep.getRegiao() == Regiao.SUDESTE;
        System.out.println("   ✓ CEP válido aceito: " + cep.getCodigo() + " - " + cep.getRegiao());
    }

    private static void testEstrategiasFrete() {
        System.out.println("\n2. Testando estratégias de frete...");

        Produto notebook = new Produto(1L, "Notebook",
                new Dinheiro("2500.00", Moeda.BRL), new BigDecimal("2.5"));

        List<ItemCarrinho> itens = List.of(new ItemCarrinho(notebook, 1));
        CEP cep = new CEP("01310-100");

        Pedido pedidoSedex = new Pedido(itens, cep, new SedexStrategy());
        BigDecimal freteSedex = pedidoSedex.calcularFrete();
        System.out.println("   ✓ SEDEX: R$ " + freteSedex);
        assert freteSedex.equals(new BigDecimal("21.25"));

        Pedido pedidoPac = new Pedido(itens, cep, new PacStrategy());
        BigDecimal fretePac = pedidoPac.calcularFrete();
        System.out.println("   ✓ PAC: R$ " + fretePac);
        assert fretePac.equals(new BigDecimal("11.75"));

        Pedido pedidoRetirada = new Pedido(itens, cep, new RetiradaNaLojaStrategy());
        BigDecimal freteRetirada = pedidoRetirada.calcularFrete();
        System.out.println("   ✓ Retirada na Loja: R$ " + freteRetirada);
        assert freteRetirada.equals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));
    }

    private static void testTrocaEstrategiaRuntime() {
        System.out.println("\n3. Testando troca de estratégia em runtime...");

        Produto mouse = new Produto(2L, "Mouse",
                new Dinheiro("150.00", Moeda.BRL), new BigDecimal("0.3"));

        List<ItemCarrinho> itens = List.of(new ItemCarrinho(mouse, 2));
        CEP cep = new CEP("90210-001"); // Sul

        Pedido pedido = new Pedido(itens, cep, new SedexStrategy());
        System.out.println("   ✓ Estratégia inicial (SEDEX): R$ " + pedido.calcularFrete());

        pedido.setEstrategiaFrete(new PacStrategy());
        System.out.println("   ✓ Após trocar para PAC: R$ " + pedido.calcularFrete());

        pedido.setEstrategiaFrete(new RetiradaNaLojaStrategy());
        System.out.println("   ✓ Após trocar para Retirada: R$ " + pedido.calcularFrete());
    }

    private static void testFretePromocional() {
        System.out.println("\n4. Testando frete promocional via lambda...");

        Produto notebook = new Produto(1L, "Notebook",
                new Dinheiro("2500.00", Moeda.BRL), new BigDecimal("2.5"));
        Produto mouse = new Produto(2L, "Mouse",
                new Dinheiro("50.00", Moeda.BRL), new BigDecimal("0.2"));

        List<ItemCarrinho> itensBaratos = List.of(new ItemCarrinho(mouse, 1));
        List<ItemCarrinho> itensCaros = List.of(new ItemCarrinho(notebook, 1));

        CEP cep = new CEP("01310-100");

        CalculadoraFrete freteGratisAcimaDe1000 = pedido -> {
            BigDecimal valorMinimo = new BigDecimal("1000.00");
            BigDecimal subtotal = pedido.calcularSubtotal().getValor();

            if (subtotal.compareTo(valorMinimo) >= 0) {
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
            }
            return new SedexStrategy().calcular(pedido);
        };

        Pedido pedidoBarato = new Pedido(itensBaratos, cep, freteGratisAcimaDe1000);
        System.out.println("   ✓ Pedido barato (Mouse R$ 50): R$ " + pedidoBarato.calcularFrete());

        Pedido pedidoCaro = new Pedido(itensCaros, cep, freteGratisAcimaDe1000);
        System.out.println("   ✓ Pedido caro (Notebook R$ 2500): R$ " + pedidoCaro.calcularFrete() + " (GRÁTIS!)");

        CalculadoraFrete descontoProgressivo = pedido -> {
            BigDecimal subtotal = pedido.calcularSubtotal().getValor();
            BigDecimal freteBase = new PacStrategy().calcular(pedido);

            if (subtotal.compareTo(new BigDecimal("500")) >= 0) {
                return freteBase.multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_EVEN);
            }
            return freteBase;
        };

        Pedido pedidoDesconto = new Pedido(itensCaros, cep, descontoProgressivo);
        System.out.println("   ✓ Com desconto 50% (acima R$ 500): R$ " + pedidoDesconto.calcularFrete());
    }
}
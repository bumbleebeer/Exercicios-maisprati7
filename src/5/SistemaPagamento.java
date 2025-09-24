import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

abstract class PagamentoException extends Exception {
    public PagamentoException(String mensagem) {
        super(mensagem);
    }
}

class PagamentoInvalidoException extends PagamentoException {
    public PagamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}

class CartaoInvalidoException extends PagamentoException {
    public CartaoInvalidoException(String mensagem) {
        super(mensagem);
    }
}

class BoletoInvalidoException extends PagamentoException {
    public BoletoInvalidoException(String mensagem) {
        super(mensagem);
    }
}

class ChavePixInvalidaException extends PagamentoException {
    public ChavePixInvalidaException(String mensagem) {
        super(mensagem);
    }
}

class ValorInvalidoException extends PagamentoException {
    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}

abstract class FormaPagamento {
    protected String identificador;
    protected String descricao;

    public FormaPagamento(String identificador, String descricao) {
        this.identificador = identificador;
        this.descricao = descricao;
    }

    protected abstract void validarPagamento() throws PagamentoException;

    protected abstract String executarProcessamento(BigDecimal valor) throws PagamentoException;

    public final String processarPagamento(BigDecimal valor) throws PagamentoException {
        validarValor(valor);
        validarPagamento();
        return executarProcessamento(valor);
    }

    protected void validarValor(BigDecimal valor) throws ValorInvalidoException {
        if (valor == null) {
            throw new ValorInvalidoException("Valor não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("Valor deve ser positivo");
        }
        if (valor.compareTo(new BigDecimal("999999.99")) > 0) {
            throw new ValorInvalidoException("Valor excede o limite máximo permitido");
        }
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getDescricao() {
        return descricao;
    }

    public abstract String getTipo();

    @Override
    public String toString() {
        return String.format("%s: %s (%s)", getTipo(), descricao, identificador);
    }
}

class CartaoCredito extends FormaPagamento {
    private String numeroCartao;
    private String nomeTitular;
    private String dataVencimento;
    private String cvv;
    private BigDecimal limite;
    private BigDecimal valorUtilizado;

    private static final Pattern PATTERN_NUMERO_CARTAO = Pattern.compile("^\\d{16}$");
    private static final Pattern PATTERN_CVV = Pattern.compile("^\\d{3,4}$");
    private static final Pattern PATTERN_DATA_VENCIMENTO = Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2})$");

    public CartaoCredito(String numeroCartao, String nomeTitular,
                         String dataVencimento, String cvv, BigDecimal limite) {
        super(numeroCartao, "Cartão de Crédito - " + nomeTitular);
        this.numeroCartao = numeroCartao;
        this.nomeTitular = nomeTitular;
        this.dataVencimento = dataVencimento;
        this.cvv = cvv;
        this.limite = limite;
        this.valorUtilizado = BigDecimal.ZERO;
    }

    @Override
    protected void validarPagamento() throws PagamentoException {
        if (numeroCartao == null || !PATTERN_NUMERO_CARTAO.matcher(numeroCartao).matches()) {
            throw new CartaoInvalidoException("Número do cartão deve conter exatamente 16 dígitos");
        }

        if (!validarLuhn(numeroCartao)) {
            throw new CartaoInvalidoException("Número do cartão inválido (falha na verificação Luhn)");
        }

        if (nomeTitular == null || nomeTitular.trim().length() < 2) {
            throw new CartaoInvalidoException("Nome do titular deve ter pelo menos 2 caracteres");
        }

        if (dataVencimento == null || !PATTERN_DATA_VENCIMENTO.matcher(dataVencimento).matches()) {
            throw new CartaoInvalidoException("Data de vencimento deve estar no formato MM/AA");
        }

        if (isCartaoVencido(dataVencimento)) {
            throw new CartaoInvalidoException("Cartão vencido");
        }

        if (cvv == null || !PATTERN_CVV.matcher(cvv).matches()) {
            throw new CartaoInvalidoException("CVV deve conter 3 ou 4 dígitos");
        }
    }

    @Override
    protected String executarProcessamento(BigDecimal valor) throws PagamentoException {
        BigDecimal novoValorUtilizado = valorUtilizado.add(valor);
        if (novoValorUtilizado.compareTo(limite) > 0) {
            throw new CartaoInvalidoException(
                    String.format("Limite insuficiente. Disponível: R$ %s",
                            limite.subtract(valorUtilizado).setScale(2, RoundingMode.HALF_UP))
            );
        }

        valorUtilizado = novoValorUtilizado;
        String numeroMascarado = "**** **** **** " + numeroCartao.substring(12);

        return String.format("Pagamento de R$ %s aprovado no cartão %s. Limite restante: R$ %s",
                valor.setScale(2, RoundingMode.HALF_UP),
                numeroMascarado,
                limite.subtract(valorUtilizado).setScale(2, RoundingMode.HALF_UP)
        );
    }

    private boolean validarLuhn(String numero) {
        int soma = 0;
        boolean alternar = false;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));

            if (alternar) {
                digito *= 2;
                if (digito > 9) {
                    digito = digito / 10 + digito % 10;
                }
            }

            soma += digito;
            alternar = !alternar;
        }

        return soma % 10 == 0;
    }

    private boolean isCartaoVencido(String dataVencimento) {
        try {
            String[] partes = dataVencimento.split("/");
            int mes = Integer.parseInt(partes[0]);
            int ano = 2000 + Integer.parseInt(partes[1]);

            LocalDate vencimento = LocalDate.of(ano, mes, 1).plusMonths(1).minusDays(1);
            return vencimento.isBefore(LocalDate.now());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String getTipo() {
        return "Cartão de Crédito";
    }
}

class Boleto extends FormaPagamento {
    private String codigoBarras;
    private LocalDate dataVencimento;
    private String beneficiario;
    private boolean pago;

    private static final Pattern PATTERN_CODIGO_BARRAS = Pattern.compile("^\\d{47}$");

    public Boleto(String codigoBarras, LocalDate dataVencimento, String beneficiario) {
        super(codigoBarras, "Boleto - " + beneficiario);
        this.codigoBarras = codigoBarras;
        this.dataVencimento = dataVencimento;
        this.beneficiario = beneficiario;
        this.pago = false;
    }

    @Override
    protected void validarPagamento() throws PagamentoException {
        if (codigoBarras == null || !PATTERN_CODIGO_BARRAS.matcher(codigoBarras).matches()) {
            throw new BoletoInvalidoException("Código de barras deve conter exatamente 47 dígitos");
        }

        if (pago) {
            throw new BoletoInvalidoException("Boleto já foi pago anteriormente");
        }

        if (beneficiario == null || beneficiario.trim().isEmpty()) {
            throw new BoletoInvalidoException("Beneficiário não pode ser vazio");
        }

        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            System.out.println("Atenção: Boleto vencido. Multa e juros podem ser aplicados.");
        }
    }

    @Override
    protected String executarProcessamento(BigDecimal valor) throws PagamentoException {
        BigDecimal valorFinal = valor;
        String observacoes = "";

        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dataVencimento, LocalDate.now());
            BigDecimal multa = valor.multiply(new BigDecimal("0.02"));
            BigDecimal juros = valor.multiply(new BigDecimal("0.001")).multiply(new BigDecimal(diasAtraso));

            valorFinal = valor.add(multa).add(juros);
            observacoes = String.format(" (Valor original: R$ %s + Multa: R$ %s + Juros: R$ %s)",
                    valor.setScale(2, RoundingMode.HALF_UP),
                    multa.setScale(2, RoundingMode.HALF_UP),
                    juros.setScale(2, RoundingMode.HALF_UP)
            );
        }

        pago = true;

        String codigoMascarado = codigoBarras.substring(0, 5) + "..." + codigoBarras.substring(42);

        return String.format("Boleto %s pago com sucesso. Valor: R$ %s%s. Beneficiário: %s",
                codigoMascarado,
                valorFinal.setScale(2, RoundingMode.HALF_UP),
                observacoes,
                beneficiario
        );
    }

    @Override
    public String getTipo() {
        return "Boleto";
    }

    public boolean isPago() {
        return pago;
    }
}

class Pix extends FormaPagamento {
    private String chavePix;
    private TipoChavePix tipoChave;
    private String descricaoPagamento;

    public enum TipoChavePix {
        CPF("CPF", "\\d{11}"),
        CNPJ("CNPJ", "\\d{14}"),
        EMAIL("E-mail", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
        TELEFONE("Telefone", "\\+55\\d{10,11}"),
        CHAVE_ALEATORIA("Chave Aleatória", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

        private final String nome;
        private final String regex;

        TipoChavePix(String nome, String regex) {
            this.nome = nome;
            this.regex = regex;
        }

        public String getNome() { return nome; }
        public String getRegex() { return regex; }
    }

    public Pix(String chavePix, TipoChavePix tipoChave, String descricaoPagamento) {
        super(chavePix, "PIX - " + tipoChave.getNome());
        this.chavePix = chavePix;
        this.tipoChave = tipoChave;
        this.descricaoPagamento = descricaoPagamento;
    }

    @Override
    protected void validarPagamento() throws PagamentoException {
        if (chavePix == null || chavePix.trim().isEmpty()) {
            throw new ChavePixInvalidaException("Chave PIX não pode ser vazia");
        }

        if (!Pattern.matches(tipoChave.getRegex(), chavePix)) {
            throw new ChavePixInvalidaException(
                    String.format("Formato inválido para chave %s. Formato esperado: %s",
                            tipoChave.getNome(), getFormatoEsperado(tipoChave))
            );
        }

        switch (tipoChave) {
            case CPF:
                if (!validarCPF(chavePix)) {
                    throw new ChavePixInvalidaException("CPF inválido");
                }
                break;
            case CNPJ:
                if (!validarCNPJ(chavePix)) {
                    throw new ChavePixInvalidaException("CNPJ inválido");
                }
                break;
        }
    }

    @Override
    protected String executarProcessamento(BigDecimal valor) throws PagamentoException {
        Random random = new Random();
        if (random.nextInt(100) < 5) {
            throw new ChavePixInvalidaException("Chave PIX temporariamente indisponível");
        }

        String idTransacao = String.format("PIX%08d", random.nextInt(100000000));

        String chaveMascarada = mascarChavePix(chavePix, tipoChave);

        return String.format("PIX de R$ %s realizado com sucesso para %s (%s). ID: %s. %s",
                valor.setScale(2, RoundingMode.HALF_UP),
                chaveMascarada,
                tipoChave.getNome(),
                idTransacao,
                descricaoPagamento != null ? "Descrição: " + descricaoPagamento : ""
        );
    }

    private boolean validarCPF(String cpf) {
        if (cpf.length() != 11) return false;

        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int[] digitos = cpf.chars().map(c -> c - '0').toArray();

            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += digitos[i] * (10 - i);
            }
            int dv1 = 11 - (soma1 % 11);
            if (dv1 >= 10) dv1 = 0;

            int soma2 = 0;
            for (int i = 0; i < 10; i++) {
                soma2 += digitos[i] * (11 - i);
            }
            int dv2 = 11 - (soma2 % 11);
            if (dv2 >= 10) dv2 = 0;

            return digitos[9] == dv1 && digitos[10] == dv2;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validarCNPJ(String cnpj) {
        if (cnpj.length() != 14) return false;

        if (cnpj.matches("(\\d)\\1{13}")) return false;

        return true;
    }

    private String mascarChavePix(String chave, TipoChavePix tipo) {
        switch (tipo) {
            case CPF:
                return chave.substring(0, 3) + ".***.***-" + chave.substring(9);
            case CNPJ:
                return chave.substring(0, 2) + ".***.***/****-" + chave.substring(12);
            case EMAIL:
                int arroba = chave.indexOf('@');
                return chave.substring(0, Math.min(3, arroba)) + "***@" + chave.substring(arroba + 1);
            case TELEFONE:
                return "+55 (**) ****-" + chave.substring(chave.length() - 4);
            case CHAVE_ALEATORIA:
                return chave.substring(0, 8) + "-****-****-****-" + chave.substring(32);
            default:
                return "***";
        }
    }

    private String getFormatoEsperado(TipoChavePix tipo) {
        switch (tipo) {
            case CPF: return "11111111111";
            case CNPJ: return "11111111111111";
            case EMAIL: return "usuario@exemplo.com";
            case TELEFONE: return "+5511999999999";
            case CHAVE_ALEATORIA: return "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
            default: return "Formato específico";
        }
    }

    @Override
    public String getTipo() {
        return "PIX";
    }
}

public class SistemaPagamento {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE FORMAS DE PAGAMENTO ===\n");

        List<FormaPagamento> formasPagamento = new ArrayList<>();

        try {
            formasPagamento.add(new CartaoCredito(
                    "4532015112830366", "João Silva", "12/25", "123", new BigDecimal("5000.00")
            ));
            formasPagamento.add(new CartaoCredito(
                    "5555555555554444", "Maria Santos", "08/24", "456", new BigDecimal("2000.00")
            ));

            formasPagamento.add(new Boleto(
                    "23791111100000001234567890123456789012345671234",
                    LocalDate.now().plusDays(30),
                    "Loja ABC Ltda"
            ));
            formasPagamento.add(new Boleto(
                    "10491111100000002234567890123456789012345671235",
                    LocalDate.now().minusDays(5),
                    "Prestadora de Serviços XYZ"
            ));

            formasPagamento.add(new Pix(
                    "12345678901",
                    Pix.TipoChavePix.CPF,
                    "Pagamento de produto"
            ));
            formasPagamento.add(new Pix(
                    "usuario@exemplo.com",
                    Pix.TipoChavePix.EMAIL,
                    "Transferência para amigo"
            ));
            formasPagamento.add(new Pix(
                    "+5511987654321",
                    Pix.TipoChavePix.TELEFONE,
                    "Pagamento de serviço"
            ));

        } catch (Exception e) {
            System.out.println("Erro ao criar formas de pagamento: " + e.getMessage());
        }

        System.out.println("1. FORMAS DE PAGAMENTO DISPONÍVEIS:");
        System.out.println("================================================================================");
        for (int i = 0; i < formasPagamento.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, formasPagamento.get(i));
        }

        System.out.println("\n2. DEMONSTRAÇÃO DE PAGAMENTOS VÁLIDOS:");
        System.out.println("================================================================================");
        demonstrarPagamentosValidos(formasPagamento);

        System.out.println("\n3. DEMONSTRAÇÃO DE TRATAMENTO DE EXCEÇÕES:");
        System.out.println("================================================================================");
        demonstrarExcecoes();

        System.out.println("\n4. SIMULAÇÃO DE COMPRA:");
        System.out.println("================================================================================");
        simularCompra(formasPagamento);

        System.out.println("\n=== FIM DA DEMONSTRAÇÃO ===");
    }

    private static void demonstrarPagamentosValidos(List<FormaPagamento> formas) {
        BigDecimal[] valores = {
                new BigDecimal("150.50"),
                new BigDecimal("299.99"),
                new BigDecimal("89.90"),
                new BigDecimal("1250.00"),
                new BigDecimal("45.00"),
                new BigDecimal("567.80"),
                new BigDecimal("123.45")
        };

        for (int i = 0; i < formas.size(); i++) {
            FormaPagamento forma = formas.get(i);
            BigDecimal valor = valores[i % valores.length];

            System.out.printf("\nProcessando pagamento via %s:\n", forma.getTipo());
            System.out.printf("   Valor: R$ %s\n", valor.setScale(2, RoundingMode.HALF_UP));

            try {
                String resultado = forma.processarPagamento(valor);
                System.out.printf("   SUCESSO: %s\n", resultado);

            } catch (PagamentoException e) {
                System.out.printf("   ERRO: %s\n", e.getMessage());
                System.out.printf("   Tipo de exceção: %s\n", e.getClass().getSimpleName());
            }
        }
    }

    private static void demonstrarExcecoes() {
        System.out.println("Testando diversos cenários de exceção:\n");

        System.out.println("Cartão com número inválido:");
        try {
            CartaoCredito cartao = new CartaoCredito("1234567890123456", "Teste", "12/25", "123", new BigDecimal("1000"));
            cartao.processarPagamento(new BigDecimal("100"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }

        System.out.println("\nCartão vencido:");
        try {
            CartaoCredito cartao = new CartaoCredito("4532015112830366", "Teste", "01/20", "123", new BigDecimal("1000"));
            cartao.processarPagamento(new BigDecimal("100"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }

        System.out.println("\nValor negativo:");
        try {
            CartaoCredito cartao = new CartaoCredito("4532015112830366", "Teste", "12/25", "123", new BigDecimal("1000"));
            cartao.processarPagamento(new BigDecimal("-100"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }

        System.out.println("\nPIX com CPF inválido:");
        try {
            Pix pix = new Pix("12345678900", Pix.TipoChavePix.CPF, "Teste");
            pix.processarPagamento(new BigDecimal("50"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }

        System.out.println("\nPIX com e-mail inválido:");
        try {
            Pix pix = new Pix("email_invalido", Pix.TipoChavePix.EMAIL, "Teste");
            pix.processarPagamento(new BigDecimal("50"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }

        System.out.println("\nBoleto com código de barras inválido:");
        try {
            Boleto boleto = new Boleto("123456789", LocalDate.now().plusDays(10), "Teste");
            boleto.processarPagamento(new BigDecimal("100"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }

        System.out.println("\nLimite de cartão excedido:");
        try {
            CartaoCredito cartao = new CartaoCredito("4532015112830366", "Teste", "12/25", "123", new BigDecimal("100"));
            cartao.processarPagamento(new BigDecimal("200"));
            System.out.println("   Deveria ter falhado!");
        } catch (Exception e) {
            System.out.printf("   Exceção capturada: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static void simularCompra(List<FormaPagamento> formas) {
        BigDecimal valorCompra = new BigDecimal("1299.99");
        System.out.printf("Simulando compra de R$ %s\n\n", valorCompra.setScale(2, RoundingMode.HALF_UP));

        System.out.println("Tentando pagar com diferentes formas:\n");

        for (FormaPagamento forma : formas) {
            System.out.printf("Tentativa com %s:\n", forma.getTipo());
            System.out.printf("   %s\n", forma.getDescricao());

            try {
                String resultado = forma.processarPagamento(valorCompra);
                System.out.printf("   SUCESSO: %s\n", resultado);
                System.out.println("   Compra finalizada com sucesso!\n");
                return;

            } catch (PagamentoException e) {
                System.out.printf("   FALHOU: %s (%s)\n",
                        e.getMessage(), e.getClass().getSimpleName());
                System.out.println("   Tentando próxima forma de pagamento...\n");
            }
        }

        System.out.println("Não foi possível finalizar a compra com nenhuma forma de pagamento!");
    }
}
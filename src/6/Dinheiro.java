import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Dinheiro {
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

    public Dinheiro subtrair(Dinheiro outro) {
        validarMoeda(outro);
        return new Dinheiro(this.valor.subtract(outro.valor), this.moeda);
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(this.valor.multiply(fator), this.moeda);
    }

    public Dinheiro multiplicar(int quantidade) {
        return multiplicar(new BigDecimal(quantidade));
    }

    public Dinheiro aplicarDesconto(BigDecimal percentual) {
        if (percentual.compareTo(new BigDecimal("30")) > 0) {
            throw new IllegalArgumentException("Desconto não pode ser maior que 30%");
        }
        BigDecimal fatorDesconto = BigDecimal.ONE.subtract(percentual.divide(new BigDecimal("100"), 4, RoundingMode.HALF_EVEN));
        return new Dinheiro(this.valor.multiply(fatorDesconto), this.moeda);
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

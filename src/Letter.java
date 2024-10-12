import java.math.BigInteger;

public class Letter implements Factor {
    private final String letter;

    public Letter(String x) {
        this.letter = x;
    }

    public Poly toPoly() {
        Mono mono = new Mono(new BigInteger("1"),new BigInteger("1"),false,null);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

    public String toString() {
        return letter;
    }
}

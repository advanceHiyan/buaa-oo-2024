import java.math.BigInteger;

public class Num implements Factor {
    private final BigInteger value;

    public Num(String num) {
        value = new BigInteger(num);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public Poly toPoly() {
        Mono mono = new Mono(value,new BigInteger("0"),false,null);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }
}

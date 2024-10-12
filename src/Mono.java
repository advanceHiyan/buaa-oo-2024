import java.math.BigInteger;

public class Mono {
    private BigInteger cment;

    private int sign = 1;

    private int ement;

    public Mono(BigInteger c,int e) {
        this.cment = c;
        this.ement = e;
    }

    public BigInteger getCment() {
        return cment;
    }

    public void checkSign(int signed) {
        sign *= signed;
    }

    public int getEment() {
        return ement;
    }

    public void addMono(BigInteger op) {
        this.cment = this.cment.add(op);
    }

    public Mono mul(Mono op) {
        Mono newmono = new Mono(this.cment.multiply(op.cment),this.ement + op.ement);
        newmono.checkSign(this.sign);
        newmono.checkSign(op.getSign());
        return newmono;
    }

    public void odverse() {
        this.cment = this.cment.multiply(BigInteger.valueOf(-1));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(cment);
        sb.append("*x^");
        sb.append(ement);
        return sb.toString();
    }

    public int getSign() {
        return sign;
    }
}

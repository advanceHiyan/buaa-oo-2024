import java.math.BigInteger;

public class Mono {
    private BigInteger cment;

    private int sign = 1;

    private BigInteger ement;

    private boolean haveEeXxPp;

    private Poly expNeiPoly;

    private boolean flag = true;

    public Mono(BigInteger c, BigInteger e, boolean haveexp, Poly expoly) {
        this.cment = c;
        this.ement = e;
        this.haveEeXxPp = haveexp;
        this.expNeiPoly = expoly;
    }

    public BigInteger getCment() {
        return cment;
    }

    public void checkSign(int signed) {
        sign *= signed;
    }

    public BigInteger getEment() {
        return ement;
    }

    public void addMono(BigInteger op) {
        this.cment = this.cment.add(op);
    }

    public Mono mul(Mono op) {
        boolean tf = false;
        Poly expoly = new Poly();
        if (op.getCment().equals(new BigInteger("0")) ||
                this.getCment().equals(new BigInteger("0"))) {
            return new Mono(new BigInteger("0"),new BigInteger("1"),false,null);
        }
        if (this.haveEeXxPp) {
            tf = this.haveEeXxPp;
            expoly.getMonos().addAll(this.expNeiPoly.getMonos());
        }
        if (op.haveEeXxPp) {
            tf = op.haveEeXxPp;
            expoly.getMonos().addAll(op.expNeiPoly.getMonos());
        }
        Mono newmono = new Mono(this.cment.multiply(op.cment),
                this.ement.add(op.ement), tf, expoly);
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
        if (haveEeXxPp) {
            sb.append("*exp(");
            sb.append(expNeiPoly.toString());
            sb.append(")");
        }
        return sb.toString();
    }

    public void checkCmul() {
        if (flag) {
            BigInteger s = new BigInteger(Integer.toString(sign));
            this.cment = this.cment.multiply(s);
            flag = false;
        }
    }

    public void checkCadd(BigInteger b) {
        this.cment = this.cment.add(b);
    }

    public int getSign() {
        return sign;
    }

    public boolean gethaveEeXxP() {
        return haveEeXxPp;
    }

    public Poly getExpNeiPoly() {
        return expNeiPoly;
    }
}

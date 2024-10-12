import java.math.BigInteger;
import java.util.ArrayList;

public class CoreFactor {
    private ArrayList<Factor> factors = new ArrayList<>();
    private int exp;

    public CoreFactor(ArrayList<Factor> factors) {
        this.factors = factors;
    }

    public String toString() {
        return "";
    }

    public Poly toPoly() {
        Poly poly = factors.get(0).toPoly();
        int exp = Integer.parseInt(factors.get(1).toString());
        if (poly.getMonos().get(0).getCment().equals(new BigInteger("0"))
                && poly.getMonos().size() == 1) {
            poly.getMonos().clear();
            poly.getMonos().add(new Mono(new BigInteger("0"),new BigInteger("0"),false,null));
            return poly;
        }
        else if (exp == 0) {
            Poly tempoly = new Poly();
            tempoly.addMono(new Mono(new BigInteger("1"),new BigInteger("0"),false,null));
            return tempoly;
        }
        else {
            Poly op = new Poly();
            op.addMonos(poly);
            for (int i = 0;i < exp - 1;i++) {
                //Poly op = poly;//!!!指针
                poly.multpoly(op);
            }
            return poly;
        }
    }

    public ArrayList<Factor> getFactors() {
        return factors;
    }
}

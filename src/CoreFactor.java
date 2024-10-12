import java.math.BigInteger;
import java.util.ArrayList;

public class CoreFactor implements Factor {
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
        if (exp == 0) {
            Poly tempoly = new Poly();
            tempoly.addMono(new Mono(new BigInteger("1"),0));
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

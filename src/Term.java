import java.math.BigInteger;
import java.util.ArrayList;

public class Term {
    private final ArrayList<CoreFactor> cfactors = new ArrayList<>();

    public Term(ArrayList<CoreFactor> factors) {
        this.cfactors.addAll(factors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(cfactors.get(0));
        for (int i = 1; i < cfactors.size(); i++) {
            sb.append("*");
            sb.append(cfactors.get(i));
        }
        return sb.toString();
    }

    public Poly topoly() {
        Poly poly = new Poly();
        poly.getMonos().addAll(cfactors.get(0).toPoly().getMonos());
        if (poly.getMonos().get(0).getCment().equals(new BigInteger("0"))
                && poly.getMonos().size() == 1) {
            poly.getMonos().clear();
            poly.getMonos().add(new Mono(new BigInteger("0"),new BigInteger("0"),false,null));
            return poly;
        }
        for (int i = 1;i < cfactors.size();i++) {
            Poly opoly = cfactors.get(i).toPoly();
            if (opoly.getMonos().get(0).getCment().equals(new BigInteger("0"))
                    && opoly.getMonos().size() == 1) {
                opoly.getMonos().clear();
                opoly.getMonos().add(new Mono(new BigInteger("0"),new BigInteger("0"),false,null));
                return opoly;
            }
            poly.multpoly(opoly);
        }
        return poly;
    }

    public ArrayList<CoreFactor> getCfactors() {
        return cfactors;
    }
}

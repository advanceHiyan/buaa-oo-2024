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
        for (int i = 1;i < cfactors.size();i++) {
            poly.multpoly(cfactors.get(i).toPoly());
        }
        return poly;
    }

    public ArrayList<CoreFactor> getCfactors() {
        return cfactors;
    }
}

import java.util.ArrayList;

public class Expr implements Factor {
    private final ArrayList<Term> terms = new ArrayList<>();

    private final ArrayList<Token> ops = new ArrayList<>();

    public Expr(ArrayList<Term> terms, ArrayList<Token> ops) {
        this.terms.addAll(terms);
        this.ops.addAll(ops);
    }

    public Poly toPoly() {
        Poly newpoly = new Poly();
        int i = 0;
        for (Term it:terms) {
            Poly opoly = it.topoly();
            if (i > 0) {
                for (Mono thismono:opoly.getMonos()) {
                    thismono.checkSign(ops.get(i - 1).opsTOsign());
                }
            }
            newpoly.getMonos().addAll(opoly.getMonos());
            i++;
        }
        return newpoly;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(terms.get(0));
        for (int i = 0;i < ops.size();i++) {
            sb.append(ops.get(i));
            sb.append(terms.get(i + 1));
        }
        sb.append(")");
        return sb.toString();
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }
}

package Factor;

public class Exp implements Factor {
    private Factor factor;

    public Exp(Factor factor) {
        this.factor = factor;
    }

    @Override
    public String toString() {
        return "exp(" + factor.toString() + ")";
    }

    @Override
    public Factor derive() {
        Term term = new Term();
        Factor fc = this.clone();
        term.addFactor(fc);
        term.addFactor(factor.derive());
        /* TODO 3 */
        return term;
    }

    @Override
    public Factor clone() {
        return new Exp(factor.clone());
    }
}

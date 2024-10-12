import java.util.ArrayList;

public class Poly {

    private ArrayList<Mono> monos = new ArrayList<>();

    public void addMono(Mono mono) {
        monos.add(mono);
    }

    public ArrayList<Mono> getMonos() {
        return monos;
    }

    public void addMonos(Poly other) {
        this.monos.addAll(other.getMonos());
    }

    public void multpoly(Poly op) {
        ArrayList<Mono> addmonos = new ArrayList<>();
        for (Mono it: monos) {
            for (Mono that:op.getMonos()) {
                Mono mono = it.mul(that);
                addmonos.add(mono);
            }
        }
        monos.clear();
        monos.addAll(addmonos);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Mono it:this.monos) {
            int sign = it.getSign();
            if (sign == 1) {
                sb.append("+");
            } else {
                sb.append("-");
            }
            sb.append(it.toString());
        }
        return sb.toString();
    }
}

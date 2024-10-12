import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
        for (Mono it : monos) {
            for (Mono that : op.getMonos()) {
                Mono mono = it.mul(that);
                addmonos.add(mono);
            }
        }
        monos.clear();
        monos.addAll(addmonos);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Mono it : this.monos) {
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

    public void qiuDao() {
        ArrayList<Mono> addmns = new ArrayList<>();
        for (Mono mono:this.monos) {
            if (mono.gethaveEeXxP()) {
                Mono n = new Mono(mono.getCment(),mono.getEment(),
                        mono.gethaveEeXxP(),mono.getExpNeiPoly());
                n.checkSign(mono.getSign());
                addmns.add(n);
                mono.dxNoExp();
            } else {
                mono.dxNoExp();
            }
        }
        ArrayList<Mono> newaddmonos = new ArrayList<>();
        for (Mono mono:addmns) {
            Poly epoly = new Poly();
            for (Mono exm:mono.getExpNeiPoly().getMonos()) {
                Mono m = new Mono(exm.getCment(),exm.getEment(),exm.gethaveEeXxP(),
                        exm.getExpNeiPoly());
                m.checkSign(exm.getSign());
                epoly.getMonos().add(m);
            }
            epoly.qiuDao();
            for (Mono qd:epoly.getMonos()) {
                Mono jia = new Mono(mono.getCment(),mono.getEment(),
                        mono.gethaveEeXxP(),mono.getExpNeiPoly());
                jia.noreMul(qd);
                newaddmonos.add(jia);
            }
        }
        monos.addAll(newaddmonos);
        this.Tidymonos();
    }

    public void Tidymonos() {
        HashMap<BigInteger,Mono> ces = new HashMap<>();
        HashMap<String, Mono> xifindstr = new HashMap<>();

        for (Mono mono : this.monos) {
            BigInteger e = mono.getEment();
            mono.checkCmul();
            if (mono.gethaveEeXxP() && mono.getCment().equals(new BigInteger("0")) == false) {
                BigInteger cnm = mono.getCment();
                String bcl = getOneExpStr(mono);
                if (xifindstr.get(bcl) == null) {
                    Mono linshi = mono;
                    Mono lianbiaoMono = new Mono(linshi.getCment(),
                            linshi.getEment(),linshi.gethaveEeXxP(),linshi.getExpNeiPoly());
                    xifindstr.put(bcl, lianbiaoMono);
                } else {
                    xifindstr.get(bcl).checkCadd(mono.getCment());
                }
                continue;
            }
            if (ces.get(e) != null) {
                ces.get(e).checkCadd(mono.getCment());
            } else {
                ces.put(e, mono);
            }
        }
        this.monos.clear();
        BigInteger zero = new BigInteger("0");
        for (BigInteger key: ces.keySet()) {
            BigInteger c = ces.get(key).getCment();
            Mono newmono = new Mono(c.abs(),ces.get(key).getEment(),false,null);
            if (c.compareTo(zero) == -1) {
                newmono.checkSign(-1);
            }
            this.monos.add(newmono);
        }
        for (String key:xifindstr.keySet()) {
            Mono newmono = new Mono(xifindstr.get(key).getCment().abs(),
                    xifindstr.get(key).getEment(), true,xifindstr.get(key).getExpNeiPoly());
            if (xifindstr.get(key).getCment().compareTo(zero) == -1) {
                newmono.checkSign(-1);
            }
            this.monos.add(newmono);
        }
    }

    public String getOneExpStr(Mono m) {
        StringBuilder sb = new StringBuilder();
        if (m.getEment().equals(new BigInteger("0"))) {
            sb.append("exp(");
            String expstr = this.FindKRofexp(m.getExpNeiPoly());
            sb.append(expstr + ")+");
            return sb.toString();
        }
        sb.append("x");
        if (m.getEment().equals(new BigInteger("1")) == false) {
            sb.append("^" + m.getEment());
        }
        sb.append("*exp(");
        String expstr = this.FindKRofexp(m.getExpNeiPoly());
        sb.append(expstr + ")+");
        return sb.toString();
    }

    public String FindKRofexp(Poly expoly) {
        FinPro fp = new FinPro(expoly);
        StringBuilder sb = new StringBuilder();
        if (fp.getsize() > 1) {
            sb.append("(");
            sb.append(fp.SimplifyToStr());
            sb.append(")");
        } else {
            sb.append("(" + fp.SimplifyToStr() + ")");
        }
        return sb.toString();
    }
}

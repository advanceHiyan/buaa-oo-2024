import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class AaaK {
    private Poly poly = new Poly();
    private HashMap<BigInteger, BigInteger> ces = new HashMap<>();

    private ArrayList<Mono> haveexpmonos = new ArrayList<>();

    public AaaK(Poly opoly) {
        this.poly = opoly;
    }

    public String SimplifyToStr() {
        HashMap<String, Mono> xifindstr = new HashMap<>();
        for (Mono mono : poly.getMonos()) {
            BigInteger e = mono.getEment();
            BigInteger sign = new BigInteger(Integer.toString(mono.getSign()));
            if (mono.gethaveEeXxP() && mono.getCment().equals(new BigInteger("0")) == false) {
                mono.checkCmul();
                BigInteger cnm = mono.getCment();
                String bcl = getOneExpStr(mono);
                if (xifindstr.get(bcl) == null) {
                    Mono linshi = mono;
                    Mono lianbiaoMono = new Mono(linshi.getCment(),
                            linshi.getEment(),linshi.gethaveEeXxP(),linshi.getExpNeiPoly());
                    xifindstr.put(bcl, lianbiaoMono);
                    haveexpmonos.add(mono);
                } else {
                    xifindstr.get(bcl).checkCadd(mono.getCment());
                }
                continue;
            }
            if (ces.get(e) != null) {
                BigInteger newc = ces.get(e).add(mono.getCment().multiply(sign));
                ces.put(e, newc);
            } else {
                ces.put(e, mono.getCment().multiply(sign));
            }
        }
        String anstr = this.Strout();
        return anstr;
    }

    public String Strout() {
        StringBuilder sb = new StringBuilder();
        for (BigInteger key : ces.keySet()) {
            if (ces.get(key).equals(new BigInteger("0"))) {
                continue;
            }
            if (key.equals(new BigInteger("0"))) {
                sb.append(ces.get(key) + "+");
                continue;
            }
            if (ces.get(key).equals(new BigInteger("1")) == false) {
                sb.append(ces.get(key) + "*");
            }
            sb.append("x");
            if (key.equals(new BigInteger("1")) == false) {
                sb.append("^" + key);
            }
            sb.append("+");
        }
        String str = sb.toString() + expStrOut();
        str = str.replace("+-", "-");
        if (str.length() == 0) {
            return "0";
        }
        return str.substring(0, str.length() - 1);
    }

    public String expStrOut() {
        StringBuilder sb = new StringBuilder();
        for (Mono m : haveexpmonos) {
            if (m.getEment().equals(new BigInteger("0"))) {
                if (m.getCment().equals(new BigInteger("1")) == false) {
                    sb.append(m.getCment() + "*");
                }
                sb.append("exp(");
                String expstr = this.FindKRofexp(m.getExpNeiPoly());
                sb.append(expstr + ")+");
                continue;
            }
            if (m.getCment().equals(new BigInteger("1")) == false) {
                sb.append(m.getCment() + "*");
            }
            sb.append("x");
            if (m.getEment().equals(new BigInteger("1")) == false) {
                sb.append("^" + m.getEment());
            }
            sb.append("*exp(");
            String expstr = this.FindKRofexp(m.getExpNeiPoly());
            sb.append(expstr + ")+");
        }
        String expstrpro = sb.toString();
        return expstrpro;
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
        AaaK fp = new AaaK(expoly);
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

    public int getsize() {
        return this.haveexpmonos.size() + this.ces.size();
    }
}

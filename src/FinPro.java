import java.math.BigInteger;
import java.util.HashMap;

public class FinPro {
    private Poly poly = new Poly();
    private HashMap<Integer,BigInteger> ces = new HashMap<>();

    public FinPro(Poly opoly) {
        this.poly = opoly;
    }

    public String SimplifyToStr() {
        for (Mono mono:poly.getMonos()) {
            int e = mono.getEment();
            BigInteger sign = new BigInteger(Integer.toString(mono.getSign()));
            if (ces.get(e) != null) {
                BigInteger newc = ces.get(e).add(mono.getCment().multiply(sign));
                ces.put(e,newc);
            } else {
                ces.put(e,mono.getCment().multiply(sign));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Integer key:ces.keySet()) {
            if (ces.get(key).equals(new BigInteger("0"))) {
                continue;
            }
            sb.append(ces.get(key));
            sb.append("*x^");
            sb.append(key);
            sb.append("+");
        }
        String str = sb.toString().replace("*x^0","");
        str = str.replace("+-","-").replace("1*","");
        //str = str.replace("^1","");!!!!!
        if (str.length() == 0) {
            return "0";
        }
        return str.substring(0, str.length() - 1);
    }
}

import java.util.ArrayList;
import java.math.BigInteger;

public class ExPexP implements Factor {

    private ArrayList<Token> tokens = new ArrayList<>();

    private int zhi;

    public ExPexP(ArrayList<Token> tks, int zhizhishu) {
        tokens.addAll(tks);  //等于还是addall！！！！！！！！！！！！！！！！
        this.zhi = zhizhishu;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        Lexer explexer = new Lexer(this.exptoString());
        Parser exparser = new Parser(explexer);
        Expr expExpr = exparser.parserExpr();
        Poly expoly = expExpr.toPoly();
        Mono mono = new Mono(new BigInteger("1"),new BigInteger("0"),true,expoly);
        poly.getMonos().add(mono);
        return poly;
    }

    public String exptoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Token tk:tokens) {
            sb.append(tk.toString());
        }
        sb.append(")*");
        sb.append(String.valueOf(zhi));
        return sb.toString();
    }
}

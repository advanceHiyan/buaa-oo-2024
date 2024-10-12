import java.util.ArrayList;
import java.util.HashMap;

public class PrePro {
    private String exprProed;

    private HashMap<Character,CustFun> funs;

    public PrePro(String expression,HashMap<Character,CustFun> inputfuns) {
        this.funs = inputfuns;

        StringBuffer sbuffer = new StringBuffer();//字符串数组
        for (String str: expression.split("[ \\t]+")) { //分割后存入数组
            sbuffer.append(str);
        }
        StringBuilder sb = new StringBuilder();
        if (sbuffer.toString().charAt(0) == '+' || sbuffer.toString().charAt(0) == '-') {
            sb.append("0");
        }
        sb.append(sbuffer.toString());
        String proing = repl(sb.toString());
        String proed = removeSpece(proFgH(proing));
        this.exprProed = repl(proed);
    }

    public String removeSpece(String expression) {
        StringBuffer sbuffer = new StringBuffer();//字符串数组
        for (String str: expression.split("[ \\t]+")) { //分割后存入数组
            sbuffer.append(str);
        }
        return sbuffer.toString();
    }

    public String proFgH(String expr) {
        int pos = findnear(expr);
        if (pos == -1) {
            return expr;
        }
        int start = pos;
        int kuohuFlag = 1;
        int fghuou = 0;
        boolean infgh = false;
        pos += 2;
        StringBuilder sb = new StringBuilder();
        ArrayList<String> truexyz = new ArrayList<>();
        while (kuohuFlag != 0) {
            Character chr = expr.charAt(pos);
            if (isFgH(chr)) {
                infgh = true;
            }
            else if (chr == '(') {
                kuohuFlag++;
                if (infgh) {
                    fghuou++;
                }
            } else if (chr == ')') {
                kuohuFlag--;
                if (infgh) {
                    fghuou--;
                }
                if (fghuou == 0) {
                    infgh = false;
                }
            }
            if ((chr == ',' && infgh == false) || kuohuFlag == 0) {
                truexyz.add(sb.toString());
                sb.delete(0,sb.length());
            } else {
                sb.append(chr.toString());
            }
            pos++;
        }
        String tihuan = funs.get(expr.charAt(start)).replaceXyz(truexyz);
        String tihuanyihou = expr.replace(expr.substring(start,pos),tihuan);
        return proFgH(tihuanyihou);
    }

    public boolean isFgH(Character chr) {
        if (chr == 'f' || chr == 'g' || chr == 'h') {
            return true;
        } else {
            return false;
        }
    }

    public int findnear(String str) {
        int p1 = str.indexOf("f");
        int p2 = str.indexOf("g");
        int p3 = str.indexOf("h");
        if (p1 == -1 && p2 == -1 && p3 == -1) {
            return -1;
        }
        p1 = Fu1to1000(p1);
        p2 = Fu1to1000(p2);
        p3 = Fu1to1000(p3);
        int ans = p1;
        if (p2 < ans) {
            ans = p2;
        }
        if (p3 < ans) {
            ans = p3;
        }
        return ans;
    }

    public int Fu1to1000(int x) {
        int t = x;
        if (x == -1) {
            t = 102400;
        }
        return t;
    }

    public String repl(String p) {
        String proing = p;
        proing = proing.replace("+++","+");
        proing = proing.replace("++","+");
        proing = proing.replace("--","+");
        proing = proing.replace("+-","-");
        proing = proing.replace("-+","-");
        proing = proing.replace("++","+");
        proing = proing.replace("--","+");
        proing = proing.replace("+-","-");
        proing = proing.replace("-+","-");
        proing = proing.replace("**","*");
        proing = proing.replace("(+","(0+");
        proing = proing.replace("(-","(0-");
        proing = proing.replace("^+","^");
        proing = proing.replace("*+","*");
        proing = proing.replace("*-","*(0-1)*");
        String out = proing;
        return out;
    }

    public String getOutput() {
        return exprProed;
    }
}

import java.util.ArrayList;

public class CustFun {
    private final ArrayList<String> xyzs = new ArrayList<>();

    private final String exprFactor;

    public CustFun(String input) {
        String expression = input;
        StringBuffer sbuffer = new StringBuffer();//字符串数组
        for (String str: expression.split("[ \\t]+")) { //分割后存入数组
            sbuffer.append(str);
        }
        String pro = sbuffer.toString();
        int st = pro.indexOf("=");
        String tempexpr;
        tempexpr = pro.substring(st + 1);
        for (int i = pro.indexOf("(") + 1;i < pro.indexOf(")");i++) {
            StringBuilder sbb = new StringBuilder();
            if (pro.charAt(i) != ',') {
                sbb.append(pro.charAt(i));
                xyzs.add(sbb.toString());
            }
        }
        tempexpr = tempexpr.replace("exp","j");
        if (xyzs.size() >= 1) {
            tempexpr = tempexpr.replace(xyzs.get(0),"(&)");
        }
        if (xyzs.size() >= 2) {
            tempexpr = tempexpr.replace(xyzs.get(1),"(|)");
        }
        if (xyzs.size() >= 3) {
            tempexpr = tempexpr.replace(xyzs.get(2),"(%)");
        }
        tempexpr = tempexpr.replace("j","exp");
        this.exprFactor = tempexpr;
    }

    public String replaceXyz(ArrayList<String> truexyZ) {
        String tempOutput = exprFactor.toString();
        if (truexyZ.size() >= 1) {
            tempOutput = tempOutput.replace("&",truexyZ.get(0));
        }
        if (truexyZ.size() >= 2) {
            tempOutput = tempOutput.replace("|",truexyZ.get(1));
        }
        if (truexyZ.size() >= 3) {
            tempOutput = tempOutput.replace("%",truexyZ.get(2));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(tempOutput);
        sb.append(")");
        return sb.toString();
    }

    public ArrayList<String> getXyzs() {
        return xyzs;
    }

    public String getExprFactor() {
        return exprFactor;
    }
}

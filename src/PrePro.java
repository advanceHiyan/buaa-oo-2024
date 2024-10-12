public class PrePro {
    private String output;

    public PrePro(String expression) {
        StringBuffer sbuffer = new StringBuffer();//字符串数组
        for (String str: expression.split("[ \\t]+")) { //分割后存入数组
            sbuffer.append(str);
        }
        StringBuilder sb = new StringBuilder();
        if (sbuffer.toString().charAt(0) == '+' || sbuffer.toString().charAt(0) == '-') {
            sb.append("0");
        }
        sb.append(sbuffer.toString());

        output = sb.toString().replace("+++","+");
        output = output.replace("++","+");
        output = output.replace("--","+");
        output = output.replace("+-","-");
        output = output.replace("-+","-");
        output = output.replace("++","+");
        output = output.replace("--","+");
        output = output.replace("+-","-");
        output = output.replace("-+","-");

        output = output.replace("**","*");
        output = output.replace("(+","(0+");
        output = output.replace("(-","(0-");
        output = output.replace("^+","^");
        output = output.replace("*+","*");
        output = output.replace("*-","*(0-1)*");
    }

    public String getOutput() {
        return output;
    }
}

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static final HashMap<Character, CustFun> sCs = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String sc = scanner.nextLine();
            CustFun cfun = new CustFun(sc);
            sCs.put(sc.charAt(0), cfun);
        }
        String expression = scanner.nextLine();

        PrePro prepro = new PrePro(expression, sCs);
        String preproed = prepro.getOutput();
        Lexer lexer = new Lexer(preproed);

        Parser parser = new Parser(lexer);

        Expr expr = parser.parserExpr();

        Poly anspoly = expr.toPoly();
        FinPro fp = new FinPro(anspoly);
        String anstr = fp.SimplifyToStr();

        System.out.println(anstr);
    }
}

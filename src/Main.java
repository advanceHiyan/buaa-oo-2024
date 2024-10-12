import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String expression = scanner.nextLine();
        PrePro prePro = new PrePro(expression);
        Lexer lexer = new Lexer(new PrePro(expression).getOutput());
        Parser parser = new Parser(lexer);


        Expr expr = parser.parserExpr();


        Poly anspoly = expr.toPoly();
        FinPro fp = new FinPro(anspoly);
        String anstr = fp.SimplifyToStr();
        System.out.println(anstr);
    }
}

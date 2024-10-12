import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parserExpr() {
        ArrayList<Term> terms = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        terms.add(parserTerm());
        while (lexer.notEnd() && (lexer.now().getType() == Token.Type.ADD
                || lexer.now().getType() == Token.Type.SUB)) {
            ops.add(lexer.now());
            lexer.move();
            terms.add(parserTerm());
        }
        return new Expr(terms, ops);
    }

    public Term parserTerm() {
        ArrayList<CoreFactor> corefactors = new ArrayList<>();
        corefactors.add(parserCore());
        while (lexer.notEnd() && lexer.now().getType() == Token.Type.MUL) {
            lexer.move();
            corefactors.add(parserCore());
        }
        return new Term(corefactors);
    }

    public CoreFactor parserCore() {
        ArrayList<Factor> factors = new ArrayList<>();
        factors.add(parserFactor());
        boolean ifExExp = false;
        while (lexer.notEnd() && lexer.now().getType() == Token.Type.EXP) {
            lexer.move();
            factors.add(parserFactor());
            ifExExp = true;
        }
        if (!ifExExp) {
            factors.add(new Num("1"));
        }
        return new CoreFactor(factors);
    }

    public Factor parserFactor() {
        if (lexer.now().getType() == Token.Type.NUM) {
            Num num = new Num(lexer.now().getContent());
            lexer.move();
            return num;
        } else if (lexer.now().getType() == Token.Type.X) {
            Letter letter = new Letter("x");
            lexer.move();
            return letter;
        } else if (lexer.now().getType() == Token.Type.L_K) {
            lexer.move();
            Expr expr = parserExpr();
            lexer.move();
            return expr;
        } else {
            System.out.println(lexer.last());
            System.out.println(lexer.now().getType());
            return null;
        }
    }
}

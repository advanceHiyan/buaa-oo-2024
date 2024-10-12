package Parser;

import Factor.Expr;
import Factor.Factor;
import Factor.Number;
import Factor.Term;
import Factor.Exp;
import Factor.Var;

import java.math.BigInteger;

public class Parser {
    private Lexer lexer;
    
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        lexer.next();
    }
    
    /**
     * 读入Expr
     */
    
    private boolean checkPos() {
        if (lexer.getCurToken() == TokenType.SUB) {
            lexer.next();
            return false;
        } else if (lexer.getCurToken() == TokenType.ADD) {
            lexer.next();
            return true;
        }
        return true;
    }
    
    public Expr parseExpr() {
        Expr expr = new Expr();
        do {
            boolean isPos = checkPos();
            Term term = parseTerm();
            if (!isPos) {
                term.addFactor(new Number("-1"));
            }
            expr.addTerm(term);
        } while ((lexer.getCurToken() == TokenType.ADD ||
                lexer.getCurToken() == TokenType.SUB));
        return expr;
    }


    /**
     * 读入指数，对于指数的处理方法是直接展开
     */
    private void expandPower(Term term, Factor factor) {
        assert lexer.getCurToken() == TokenType.POW;
        lexer.next();
        assert lexer.getCurToken() == TokenType.NUM;
        BigInteger power = new BigInteger(lexer.getCurString());
        for (BigInteger i = BigInteger.ZERO; i.compareTo(power) < 0; i = i.add(BigInteger.ONE)) {
            term.addFactor(factor.clone());
        }
        if (power.equals(BigInteger.ZERO)) {
            term.addFactor(new Number("1"));
        }
        lexer.next();
    }

    /**
     * 读入Term
     */
    public Term parseTerm() {
        Term term = new Term();
        do {
            if (lexer.getCurToken() == TokenType.MUL) {
                lexer.next();
            }
            Factor factor = parseFactor();
            if (lexer.getCurToken() == TokenType.POW) {
                expandPower(term, factor);
            } else {
                term.addFactor(factor);
            }
        } while (lexer.getCurToken() == TokenType.MUL);
        return term;
    }
    
    /**
     * 读入Factor
     */
    public Factor parseFactor() {
        switch (lexer.getCurToken()) {
            case LBRACE:
                // (
                lexer.next();
                // expr
                Factor expr = parseExpr();
                // )
                lexer.next();
                return expr;
            case NUM:
                Factor num = new Number(lexer.getCurString());
                lexer.next();
                return num;
            case VAR:
                Factor var = new Var(lexer.getCurString());
                lexer.next();
                return var;
            case EXP:
                // exp
                lexer.next();
                // (
                lexer.next();
                // factor
                Factor f1 = parseFactor();
                // )
                lexer.next();
                return new Exp(f1);
        }
        /*
        exception
         */
        return null;
    }
}

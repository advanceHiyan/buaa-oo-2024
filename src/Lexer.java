import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens = new ArrayList<>();

    private final String expr;
    private int cur = 0;

    public Lexer(String input) {
        int pos = 0;
        this.expr = input;
        while (pos < input.length()) {
            if (input.charAt(pos) == '(') {
                tokens.add(new Token(Token.Type.L_K,"("));
                pos++;
            } else if (input.charAt(pos) == ')') {
                tokens.add(new Token(Token.Type.R_K,")"));
                pos++;
            } else if (input.charAt(pos) == '+') {
                tokens.add(new Token(Token.Type.ADD,"+"));
                pos++;
            } else if (input.charAt(pos) == '-') {
                tokens.add(new Token(Token.Type.SUB,"-"));
                pos++;
            } else if (input.charAt(pos) == '*') {
                tokens.add(new Token(Token.Type.MUL,"*"));
                pos++;
            } else if (input.charAt(pos) == '^') {
                tokens.add(new Token(Token.Type.EXP,"^"));
                pos++;
            } else if (input.charAt(pos) == 'x') {
                tokens.add(new Token(Token.Type.X,"x"));
                pos++;
            } else {
                char now = input.charAt(pos);
                StringBuffer sb = new StringBuffer();
                while (now >= '0' && now <= '9') {
                    sb.append(now);
                    pos++;
                    if (pos >= input.length()) {
                        break;
                    }
                    now = input.charAt(pos);
                }
                tokens.add(new Token(Token.Type.NUM,sb.toString()));
            }
        }
    }

    public void move() {
        cur++;
    }

    public Token now() {
        return tokens.get(cur);
    }

    public Token last() {
        return tokens.get(cur - 1);
    }

    public boolean notEnd() {
        return cur < tokens.size();
    }

    public int getCur() {
        return cur;
    }
}

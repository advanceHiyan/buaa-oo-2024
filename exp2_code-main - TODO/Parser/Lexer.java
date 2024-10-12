package Parser;

public class Lexer {
    private String input;
    private int pos;
    private TokenType curToken;
    private String curString;
    
    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.curToken = null;
        this.curString = null;
    }
    
    public TokenType getCurToken() {
        return curToken;
    }
    
    public String getCurString() {
        return curString;
    }
    
    private void passBlank() {
        while (pos < input.length() &&
                (input.charAt(pos) == ' ' ||
                        input.charAt(pos) == '\t')) {
            pos++;
        }
    }
    
    private void getChar() {
        switch (input.charAt(pos)) {
            case '+':
                curToken = TokenType.ADD;
                break;
            case '-':
                curToken = TokenType.SUB;
                break;
            case 'x':
                curToken = TokenType.VAR;
                break;
            case '*':
                curToken = TokenType.MUL;
                break;
            case '(':
                curToken = TokenType.LBRACE;
                break;
            case ')':
                curToken = TokenType.RBRACE;
                break;
        }
        curString = String.valueOf(input.charAt(pos));
        pos++;
    }
    
    private void getPow() {
        curToken = TokenType.POW;
        curString = "^";
        pos += 1;
    }

    private void getExp() {
        curToken = TokenType.EXP;
        curString = "exp";
        pos += 3;
    }

    private void getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() &&
                ("0123456789".indexOf(input.charAt(pos)) != -1)) {
            sb.append(input.charAt(pos));
            pos++;
        }
        curString = sb.toString();
        curToken = TokenType.NUM;
    }
    
    public void next() {
        passBlank();
        if (pos >= input.length()) {
            curToken = TokenType.EOL;
            curString = null;
            return;
        }
        switch (input.charAt(pos)) {
            case '+':
            case '-':
            case '(':
            case ')':
            case 'x':
                getChar();
                break;
            case '*':
                getChar();
                break;
            case '^':
                getPow();
                break;
            case 'e':
                if (input.charAt(pos + 1) == 'x' &&
                        input.charAt(pos + 2) == 'p') {
                    getExp();
                }
                break;
            default:
                getNumber();
        }
    }
}

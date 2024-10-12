public class Token {
    private final Type type;

    private final String content;

    public Token(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public enum Type {
        ADD,SUB,MUL,EXP,
        L_K,R_K,
        NUM,X,
        ThreeExp
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return content;
    }

    public int opsTOsign() {
        if (this.type == Type.ADD) {
            return 1;
        } else if (this.type == Type.SUB) {
            return -1;
        }
        else {
            return 404;
        }
    }
}

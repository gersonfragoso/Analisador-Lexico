

public class Token {
    private TokenType type;
    private String value;
    private int line;
    private int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
    public String toString() {
        if (type == TokenType.ERROR) {
            return "Erro léxico na linha " + line + ", coluna " + column + ": " + value;
        }
        return type + (value != null ? "('" + value + "')" : "") + " na linha " + line + ", coluna " + column;
    }
}


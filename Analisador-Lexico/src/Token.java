import java.util.regex.Matcher;

public class Token {
    private final String token;
    private String classification;
    private final int line;

    public Token(String token, String classification, int line) {
        this.token = token;
        this.classification = classification;
        this.line = line;
    }

    public String getToken() {
        return this.token;
    }

    public String getClassification() {
        return this.classification;
    }

    public void setClassification(String novaClassificação){
        this.classification = novaClassificação;
    }

    public int getLine() {
        return this.line;
    }

    public static String getTokenClassification(Matcher matcher) {
        if (matcher.group("COMMENT") != null) {
            return "COMMENT";
        } else if (matcher.group("KEYWORD") != null) {
            return "KEYWORD";
        } else if (matcher.group("BOOLEAN") != null) {
            return "BOOLEAN";
        } else if (matcher.group("REAL") != null) {
            return "REAL";
        } else if (matcher.group("INTEGER") != null) {
            return "INTEGER";
        } else if (matcher.group("ASSIGNMENT") != null) {
            return "ASSIGNMENT";
        } else if (matcher.group("RELATIONAL") != null) {
            return "RELATIONAL";
        } else if (matcher.group("ADDITIVE") != null) {
            return "ADDITIVE";
        } else if (matcher.group("MULTIPLICATION") != null) {
            return "MULTIPLICATION";
        } else if (matcher.group("IDENTIFIER") != null) {
            return "IDENTIFIER";
        } else if (matcher.group("DELIMITER") != null) {
            return "DELIMITER";
        } else if (matcher.group("ERROR") != null) {
            return "ERROR";
        } else {
            return "";
        }
    }
}

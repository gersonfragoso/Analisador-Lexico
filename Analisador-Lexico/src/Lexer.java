import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public static void errorMsg(String errorType, String errorToken, int errorLine) {
        if (errorType.equals("ERROR")) {
            System.out.println("ERROR: " + errorType + " " + errorToken + " IN LINE " + errorLine);
        } else {
            System.out.println("ERROR: " + errorType + " BUT NOT CLOSED IN LINE " + errorLine);
        }
    }

    private static Pattern buildRegex() {
        String regexPattern =
                "(?<COMMENT>\\/\\*.*?\\*\\/)|"
                        + "(?<KEYWORD>\\b(program|var|integer|real|boolean|procedure|begin|end|if|then|else|while|do|not)\\b)|"
                        + "(?<BOOLEAN>\\b(true|false)\\b)|"
                        + "(?<REAL>\\b\\d+\\.\\d*)|"
                        + "(?<INTEGER>\\b\\d+)|"
                        + "(?<ASSIGNMENT>:=)|"
                        + "(?<RELATIONAL><=|>=|<>|>|<|=)|"
                        + "(?<ADDITIVE>\\+|-|\\bor\\b)|"
                        + "(?<MULTIPLICATION>\\*|\\/|\\band\\b)|"
                        + "(?<IDENTIFIER>[a-zA-Z][a-zA-Z0-9_]*\\b)|"
                        + "(?<DELIMITER>[;.:(,),])|"
                        + "(?<ERROR>[^A-Za-z0-9=<>:;_+\\-*/{}\\t\\s.])";

        return Pattern.compile(regexPattern);
    }

    public static List<Token> match(String dataInput) {
        // Construir regex para ser usada no loop de correspondência
        Pattern regex = buildRegex();

        // Lista de tokens e classificações
        List<Token> tokens = new ArrayList<>();

        // Tokens que não serão incluídos no resultado
        String[] nonListableTokens = {"ERROR", "COMMENT"};

        // Linha usada no analisador léxico
        int line = 1; // Inicia com 1 para contar a primeira linha

        // Para cada correspondência de objeto em uma iteração de encontrar dentro dos dados do arquivo
        Matcher matcher = regex.matcher(dataInput);
        int startIndex = 0;
        while (matcher.find()) {
            line += countOccurrences(dataInput.substring(startIndex, matcher.start()), '\n');
            startIndex = matcher.end();
            // Obtém o token e sua classificação
            String token = "";
            String classification = "";

            if (isCommentDelimitedByCurlyBraces(dataInput, matcher.start())) {
                continue; // Ignora comentários delimitados por {}
            }

            // Verifica se o token é um comentário ou erro
            for (String tokenType : nonListableTokens) {
                if (matcher.group(tokenType) != null) {
                    classification = tokenType;
                    if (tokenType.equals("ERROR")) {
                        errorMsg(tokenType, matcher.group(tokenType), line);
                    }
                }
            }

            // Se não for um comentário ou erro, obtemos o token e sua classificação
            if (classification.isEmpty()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        token = matcher.group(i);
                        classification = Token.getTokenClassification(matcher);

                        break;
                    }
                }
            }

            // Adiciona o token à lista
            tokens.add(new Token(token, classification, line));

        }

        // Retorna os tokens, classificações e linha
        return tokens;
    }

    public static int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
    public static boolean isCommentDelimitedByCurlyBraces(String input, int startIndex) {
        int openBracesCount = 0;
        for (int i = startIndex - 1; i >= 0; i--) {
            char currentChar = input.charAt(i);
            if (currentChar == '}') {
                openBracesCount++;
            } else if (currentChar == '{') {
                if (openBracesCount == 0) {
                    return true; // Encontrou um comentário delimitado por {}
                } else {
                    openBracesCount--;
                }
            }
        }
        return false; // Não encontrou um comentário delimitado por {}
    }
}
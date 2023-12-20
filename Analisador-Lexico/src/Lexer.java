
import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private final String entrada;
    private int posicao;
    private int linha;
    private int colunas;
    private final ArrayList<Token> tokens;

    private final HashMap<String, TokenType> palavrasReservadas = new HashMap<>();
    private final HashMap<String, OP> operadoresAditivosEMultiplicativos = new HashMap<>();


    {
        // Palavras reservadas
        palavrasReservadas.put("program", TokenType.PROGRAMA);
        palavrasReservadas.put("var", TokenType.VARIAVEL);
        palavrasReservadas.put("integer", TokenType.INTEIRO);
        palavrasReservadas.put("real", TokenType.DECIMAL);
        palavrasReservadas.put("boolean", TokenType.BOOLEAN);
        palavrasReservadas.put("procedure", TokenType.PROCEDIMENTO);
        palavrasReservadas.put("begin", TokenType.INICIO);
        palavrasReservadas.put("end", TokenType.FIM);
        palavrasReservadas.put("if", TokenType.IF);
        palavrasReservadas.put("then", TokenType.THEN);
        palavrasReservadas.put("else", TokenType.ELSE);
        palavrasReservadas.put("while", TokenType.WHILE);
        palavrasReservadas.put("do", TokenType.DO);
        palavrasReservadas.put("not", TokenType.NOT);
        palavrasReservadas.put(":=", TokenType.ATRIBUICAO);
        palavrasReservadas.put("error", TokenType.ERROR);
        // Operadores Relacionais
        palavrasReservadas.put("=", TokenType.IGUAL);
        palavrasReservadas.put("<", TokenType.MENOR);
        palavrasReservadas.put(">", TokenType.MAIOR);
        palavrasReservadas.put("<=", TokenType.MENOR_OU_IGUAL);
        palavrasReservadas.put(">=", TokenType.MAIOR_OU_IGUAL);
        palavrasReservadas.put("<>", TokenType.DIFERENTE);
        // Operadores
        operadoresAditivosEMultiplicativos.put("+", OP.SOMA);
        operadoresAditivosEMultiplicativos.put("-", OP.MENOS);
        operadoresAditivosEMultiplicativos.put("*", OP.MULTIPLICACAO);
        operadoresAditivosEMultiplicativos.put("and", OP.AND);
        operadoresAditivosEMultiplicativos.put("/", OP.DIVISAO);
    }

    public Lexer(String input) {
        this.entrada = input;
        this.posicao = 0;
        this.linha = 1;
        this.colunas = 1;
        this.tokens = new ArrayList<>();
    }

    public ArrayList<Token> tokenize() {
        while (posicao < entrada.length()) {
            char currentChar = entrada.charAt(posicao);
            if (Character.isLetter(currentChar) || currentChar == '_') {
                identificarParser();
            } else if (Character.isDigit(currentChar)) {
                parseNumber();
            } else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
                operadorParse();
            } else if (currentChar == ':') {
                if (posicao + 1 < entrada.length() && entrada.charAt(posicao + 1) == '=') {
                    addToken(TokenType.ATRIBUICAO, String.valueOf(":="));
                    posicao += 2;
                    colunas += 2;
                } else {
                    addToken(TokenType.DELIMITADOR, String.valueOf(currentChar));
                    posicao++;
                    colunas++;
                }
            } else if (currentChar == '>') {
                if (posicao + 1 < entrada.length() && entrada.charAt(posicao + 1) == '=') {
                    addToken(TokenType.MAIOR_OU_IGUAL);
                    posicao += 2;
                    colunas += 2;
                } else {
                    addToken(TokenType.MAIOR);
                    posicao++;
                    colunas++;
                }
            } else if (currentChar == '<') {
                if (posicao + 1 < entrada.length() && entrada.charAt(posicao + 1) == '=') {
                    addToken(TokenType.MENOR_OU_IGUAL);
                    posicao += 2;
                    colunas += 2;
                } else if (currentChar == '>') {
                    addToken(TokenType.DIFERENTE);
                    posicao += 2;
                    colunas += 2;
                } else {
                    addToken(TokenType.MENOR);
                    posicao++;
                    colunas++;
                }
            } else if (currentChar == '(' || currentChar == ')' || currentChar == ';' || currentChar == ',' || currentChar == '.') {
                addToken(TokenType.DELIMITADOR, String.valueOf(currentChar)); // Ajuste aqui
                posicao++;
                colunas++;
            } else if (currentChar == ' ' || currentChar == '\t') {
                posicao++;
                colunas++;
            } else if (currentChar == '\n' || currentChar == '\r') {
                posicao++;
                colunas = 1;
                linha++;
            } else if (currentChar == '{') {
                pularComentario();
            } else {
                addErrorToken("Caractere inválido '" + currentChar + "'.");
                posicao++;
                colunas++;
            }
        }
        return tokens;
    }


    // parse dos operadores
    private void operadorParse() {
        char currentChar = entrada.charAt(posicao);
        String operator = String.valueOf(currentChar);

        OP op = operadoresAditivosEMultiplicativos.get(operator);
        if (op != null) {
            addToken(getTipoDoTokenParse(op));
            posicao++;
            colunas++;
        } else {
            addErrorToken("Caractere inválido '" + currentChar + "'.");
            posicao++;
            colunas++;
        }
    }

    // Identificando as operações
    private TokenType getTipoDoTokenParse(OP op) {
        switch (op) {
            case SOMA:
                return TokenType.SOMA;
            case MENOS:
                return TokenType.MENOS;
            case MULTIPLICACAO:
                return TokenType.MULTIPLICACAO;
            case DIVISAO:
                return TokenType.DIVISAO;
            default:
                return TokenType.ERROR;
        }
    }

    // Identificando número
    private void parseNumber() {
        StringBuilder number = new StringBuilder();

        while (posicao < entrada.length()) {
            char currentChar = entrada.charAt(posicao);

            if (Character.isDigit(currentChar) || currentChar == '.') {
                number.append(currentChar);
                posicao++;
                colunas++;
            } else {
                break;
            }
        }

        String numberStr = number.toString();

        if (verificarNumeroValido(numberStr)) {
            if (numberStr.contains(".")) {
                addToken(TokenType.DECIMAL, numberStr);
            } else {
                addToken(TokenType.INTEIRO, numberStr);
            }
        } else {
            addErrorToken("Número inválido: " + numberStr);
        }
    }
    //parser para identificar se é uma palvra reservada ou um identificador
    //porem primeiro ele junta a string para depois verificar
    private void identificarParser() {
        StringBuilder identifier = new StringBuilder();

        while (posicao < entrada.length()) {
            char currentChar = entrada.charAt(posicao);
            if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                identifier.append(currentChar);
                posicao++;
                colunas++;
            } else {
                break;
            }
        }

        String identifierStr = identifier.toString();

        TokenType tokenType = palavrasReservadas.get(identifierStr);
        if (tokenType != null) {
            // É uma palavra reservada
            addToken(tokenType, identifierStr);
        } else {
            //é um identificador
            addToken(TokenType.IDENTIFICADOR, identifierStr);
        }
    }


    // Pular o comentário
    private void pularComentario() {
        while (posicao < entrada.length() && entrada.charAt(posicao) != '\n' && entrada.charAt(posicao) != '\r') {
            posicao++;
        }
    }

    // Verificando números válidos
    private boolean verificarNumeroValido(String number) {
        if (!number.matches(".*\\d+.*")) {
            return false;
        }

        if (number.startsWith(".")) {
            return false;
        }

        if (number.endsWith(".")) {
            return false;
        }

        return number.matches("[+-]?[0-9]*\\.?[0-9]+");
    }

    // Adicionando os tokens
    private void addToken(TokenType type) {
        Token token = new Token(type, null, linha, colunas);
        tokens.add(token);
    }

    private void addToken(TokenType type, String value) {
        Token token = new Token(type, value, linha, colunas);
        tokens.add(token);
    }
    private void addErrorToken(String message) {
        Token errorToken = new Token(TokenType.ERROR, message, linha, colunas);
        tokens.add(errorToken);
    }

}

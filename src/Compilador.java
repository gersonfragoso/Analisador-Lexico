import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compilador { //criando o compilador
    public String entrada;
    public int posicaoAtual;
    public int linhaAtual;
    public int colunaAtual;

    public List<Token> tokens;
    public Map<String,TokenType> gramatica;

    public Compilador(String entrada) {
        this.entrada = entrada;
        this.colunaAtual = 1;
        this.posicaoAtual = 0;
        this.linhaAtual = 1;
        this.tokens = new ArrayList<>(); // Adicione esta linha
    }
    public void inicializarPalavrasReservadas() {
        gramatica = new HashMap<>();
        gramatica.put("INTEIRO", TokenType.INTEIRO);
        gramatica.put("REAL", TokenType.REAL);
        gramatica.put("VARIAVEL", TokenType.VARIAVEL);
        gramatica.put("NUMINT", TokenType.NUMINT);
        gramatica.put("NUMREAL", TokenType.NUMREAL);
        gramatica.put("LEFT_PAR", TokenType.LEFT_PAR);
        gramatica.put("RIGHT_PAR", TokenType.RIGHT_PAR);
        gramatica.put("E", TokenType.E);
        gramatica.put("OU", TokenType.OU);
        gramatica.put("LER", TokenType.LER);
        gramatica.put("IMPRIMIR", TokenType.IMPRIMIR);
        gramatica.put("CADEIA", TokenType.CADEIA);
        gramatica.put("SE", TokenType.SE);
        gramatica.put("ENTAO", TokenType.ENTAO);
        gramatica.put("SENAO", TokenType.SENAO);
        gramatica.put("ENQUANTO", TokenType.ENQUANTO);
        gramatica.put("INICIO", TokenType.INICIO);
        gramatica.put("FIM", TokenType.FIM);
    }
    private void pularEspacosEmBranco() {
        while (posicaoAtual < entrada.length() && Character.isWhitespace(entrada.charAt(posicaoAtual))) {
            if (entrada.charAt(posicaoAtual) == '\n') {
                linhaAtual++;
                colunaAtual = 1;
            } else {
                colunaAtual++;
            }
            posicaoAtual++;
        }
    }
    public List<Token> tokenize() {
        while (posicaoAtual < entrada.length()) {
            char currentChar = entrada.charAt(posicaoAtual);
            if (Character.isWhitespace(currentChar)) {
                pularEspacosEmBranco();
            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                tokenizarIdentificadorOuPalavraReservada();
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
                tokenizarNumero();
            } else if (isOperador(currentChar)) {
                tokenizarOperador();
            } else if (currentChar == ':') {
                adicionarToken(TokenType.DOIS_PONTOS, ":");
            } else {
                // Erro léxico, caractere não permitido
                throw new RuntimeException("Erro léxico na linha " + linhaAtual + ", coluna " + colunaAtual +
                        ": Caractere não permitido");
            }
        }

        return tokens;
    }
    private void tokenizarIdentificadorOuPalavraReservada() {
        StringBuilder identificador = new StringBuilder();
        while (posicaoAtual < entrada.length() &&
                (Character.isLetterOrDigit(entrada.charAt(posicaoAtual)) || entrada.charAt(posicaoAtual) == '_')) {
            identificador.append(entrada.charAt(posicaoAtual));
            posicaoAtual++;
            colunaAtual++;
        }
        String identificadorStr = identificador.toString();
        TokenType tokenType = gramatica.getOrDefault(identificadorStr, TokenType.ID);
        adicionarToken(tokenType, identificadorStr);
    }

    // Função para tokenizar números inteiros ou de ponto flutuante
    private void tokenizarNumero() {
        StringBuilder numero = new StringBuilder();
        boolean possuiPontoDecimal = false;

        while (posicaoAtual < entrada.length() && (Character.isDigit(entrada.charAt(posicaoAtual)) ||
                (entrada.charAt(posicaoAtual) == '.' && !possuiPontoDecimal))) {
            char currentChar = entrada.charAt(posicaoAtual);
            if (currentChar == '.') {
                possuiPontoDecimal = true;
            }
            numero.append(currentChar);
            posicaoAtual++;
            colunaAtual++;
        }

        String numeroStr = numero.toString();
        if (possuiPontoDecimal) {
            adicionarToken(TokenType.NUMREAL, numeroStr);
        } else {
            adicionarToken(TokenType.NUMINT, numeroStr);
        }
    }

    // Função para tokenizar operadores
    private void tokenizarOperador() {
        char currentChar = entrada.charAt(posicaoAtual);
        String operador = String.valueOf(currentChar);
        posicaoAtual++;
        colunaAtual++;

        adicionarToken(getTipoDeTokenOperador(operador), operador);
    }

    // Verifica se o caractere é um operador
    private boolean isOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' ||
                c == '>' || c == '<' || c == '!' || c == '=';
    }

    // Obtém o tipo de token para um operador
    private TokenType getTipoDeTokenOperador(String operador) {
        switch (operador) {
            case "+":
                return TokenType.SOMA;
            case "-":
                return TokenType.SUBTRACAO;
            case "*":
                return TokenType.MULTIPLICACAO;
            case "/":
                return TokenType.DIVISAO;
            case ">":
                return TokenType.MAIOR_QUE;
            case ">=":
                return TokenType.MAIOR_OU_IGUAL_QUE;
            case "<":
                return TokenType.MENOR_QUE;
            case "<=":
                return TokenType.MENOR_OU_IGUAL_QUE;
            case "!=":
                return TokenType.DIFERENTE;
            case "==":
                return TokenType.IGUAL;
            default:
                return TokenType.DESCONHECIDO;
        }
    }

    // Função para adicionar um token à lista de tokens
    private void adicionarToken(TokenType tipo, String valor) {
        tokens.add(new Token(tipo, valor, linhaAtual, colunaAtual - valor.length()));
    }
    class Token {
        private TokenType tipo;
        private String valor;
        private int linha;
        private int coluna;

        public Token(TokenType tipo, String valor, int linha, int coluna) {
            this.tipo = tipo;
            this.valor = valor;
            this.linha = linha;
            this.coluna = coluna;
        }

        public TokenType getTipo() {
            return tipo;
        }

        public String getValor() {
            return valor;
        }

        public int getLinha() {
            return linha;
        }

        public int getColuna() {
            return coluna;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "tipo=" + tipo +
                    ", valor='" + valor + '\'' +
                    ", linha=" + linha +
                    ", coluna=" + coluna +
                    '}';
        }
    }
}

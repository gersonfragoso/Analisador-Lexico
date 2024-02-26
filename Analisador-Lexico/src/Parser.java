import java.util.ArrayList;

public class Parser {
    private final ArrayList<Token> tokens;
    private int currentTokenIndex;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    public void parse() {
        programa();
        if (!match(TokenType.DELIMITADOR, ".")) {
            error("Esperado '.' no final do programa.");
        }
    }

    private Token consume() {
        return tokens.get(currentTokenIndex++);
    }

    private boolean match(TokenType type, String value) {
        if (currentTokenIndex < tokens.size()) {
            Token token = tokens.get(currentTokenIndex);
            if (token.getType() == type && (value == null || token.getValue().equals(value))) {
                currentTokenIndex++;
                return true;
            }
        }
        return false;
    }

    private void error(String message) {
        System.err.println("Erro de sintaxe na linha " + tokens.get(currentTokenIndex).getLine() +
                ", coluna " + tokens.get(currentTokenIndex).getColumn() + ": " + message);
        System.exit(1);
    }

    private void programa() {
        if (!match(TokenType.PROGRAMA, null)) {
            error("Esperado 'program'");
        }
        if (!match(TokenType.IDENTIFICADOR, null)) {
            error("Esperado identificador");
        }
        if (!match(TokenType.DELIMITADOR, ";")) {
            error("Esperado ';'");
        }
        declaracoes_variaveis();
        declaracoes_de_subprogramas();
        comando_composto();
    }

    private void declaracoes_variaveis() {
        if (match(TokenType.VARIAVEL, null)) {
            lista_declaracoes_variaveis();
        }
    }

    private void lista_declaracoes_variaveis() {
        do {
            lista_de_identificadores();
            if (!match(TokenType.DELIMITADOR, ":")) {
                error("Esperado ':'");
            }
            tipo();
            if (!match(TokenType.DELIMITADOR, ";")) {
                error("Esperado ';'");
            }
        } while (match(TokenType.IDENTIFICADOR, null));
    }

    private void lista_de_identificadores() {
        do {
            if (!match(TokenType.IDENTIFICADOR, null)) {
                error("Esperado identificador");
            }
        } while (match(TokenType.DELIMITADOR, ","));
    }

    private void tipo() {
        if (!match(TokenType.INTEIRO, null) && !match(TokenType.DECIMAL, null) &&
                !match(TokenType.BOOLEAN, null)) {
            error("Esperado tipo (integer, real ou boolean)");
        }
    }

    private void declaracoes_de_subprogramas() {
        while (match(TokenType.PROCEDIMENTO, null)) {
            if (!match(TokenType.IDENTIFICADOR, null)) {
                error("Esperado identificador");
            }
            argumentos();
            declaracoes_variaveis();
            declaracoes_de_subprogramas();
            comando_composto();
        }
    }

    private void argumentos() {
        if (match(TokenType.DELIMITADOR, "(")) {
            lista_de_parametros();
            if (!match(TokenType.DELIMITADOR, ")")) {
                error("Esperado ')'");
            }
        }
    }

    private void lista_de_parametros() {
        if (match(TokenType.DELIMITADOR, "(")) {
            lista_de_identificadores();
            if (!match(TokenType.DELIMITADOR, ":")) {
                error("Esperado ':'");
            }
            tipo();
            while (match(TokenType.DELIMITADOR, ";")) {
                lista_de_identificadores();
                if (!match(TokenType.DELIMITADOR, ":")) {
                    error("Esperado ':'");
                }
                tipo();
            }
            if (!match(TokenType.DELIMITADOR, ")")) {
                error("Esperado ')'");
            }
        }
    }

    private void comando_composto() {
        if (!match(TokenType.INICIO, null)) {
            error("Esperado 'begin'");
        }
        comandos_opcionais();
        if (!match(TokenType.FIM, null)) {
            error("Esperado 'end'");
        }
    }

    private void comandos_opcionais() {
        while (!match(TokenType.DELIMITADOR, ".") && !match(TokenType.ERROR, null)) {
            if (match(TokenType.COMENTARIO, null)) {
                consume();
            } else {
                lista_de_comandos();
            }
        }
    }

    private void lista_de_comandos() {
        do {
            comando();
        } while (match(TokenType.DELIMITADOR, ";"));
    }

    private void comando() {
        if (match(TokenType.IDENTIFICADOR, null)) {
            if (match(TokenType.ATRIBUICAO, ":=")) {
                expressao();
            } else if (match(TokenType.DELIMITADOR, "(")) {
                lista_de_expressoes();
                if (!match(TokenType.DELIMITADOR, ")")) {
                    error("Esperado ')'");
                }
            } else {
                error("Comando inválido");
            }
        } else if (match(TokenType.PROCEDIMENTO, null)) {
            if (!match(TokenType.IDENTIFICADOR, null)) {
                error("Esperado identificador");
            }
            if (match(TokenType.DELIMITADOR, "(")) {
                lista_de_expressoes();
                if (!match(TokenType.DELIMITADOR, ")")) {
                    error("Esperado ')'");
                }
            }
        } else if (match(TokenType.INICIO, null)) {
            comando_composto();
        } else if (match(TokenType.IF, null)) {
            expressao();
            if (!match(TokenType.THEN, null)) {
                error("Esperado 'then'");
            }
            comando();
            parte_else();
        } else if (match(TokenType.WHILE, null)) {
            expressao();
            if (!match(TokenType.DO, null)) {
                error("Esperado 'do'");
            }
            comando();
        } else if (match(TokenType.FOR, null)) {
            if (!match(TokenType.IDENTIFICADOR, null)) {
                error("Esperado identificador após 'for'");
            }
            if (!match(TokenType.ATRIBUICAO, ":=")) {
                error("Esperado ':=' após identificador no comando 'for'");
            }
            expressao();
            if (!match(TokenType.TO, null)) {
                error("Esperado 'to' após expressão no comando 'for'");
            }
            expressao();
            if (!match(TokenType.DO, null)) {
                error("Esperado 'do' após expressão 'to' no comando 'for'");
            }
            comando_composto();
        } else {
            error("Comando inválido");
        }
    }

    private void parte_else() {
        if (match(TokenType.ELSE, null)) {
            comando();
        }
    }

    private void expressao() {
        expressao_simples();
        if (match(TokenType.IGUAL, null) || match(TokenType.MENOR, null) ||
                match(TokenType.MAIOR, null) || match(TokenType.MENOR_OU_IGUAL, null) ||
                match(TokenType.MAIOR_OU_IGUAL, null) || match(TokenType.DIFERENTE, null)) {
            match(TokenType.SOMA, null);
            match(TokenType.MENOS, null);
            match(TokenType.OR, null);
            expressao_simples();
        }
    }


    private void expressao_simples() {
        termo();
        while (match(TokenType.SOMA, null) || match(TokenType.MENOS, null) ||
                match(TokenType.OR, null)) {
            termo();
        }
    }

    private void termo() {
        fator();
        while (match(TokenType.MULTIPLICACAO, null) || match(TokenType.DIVISAO, null) || match(TokenType.AND, null)) {
            fator();
        }
    }

    private void fator() {
        if (match(TokenType.IDENTIFICADOR, null)) {
            if (match(TokenType.DELIMITADOR, "(")) {
                lista_de_expressoes();
                if (!match(TokenType.DELIMITADOR, ")")) {
                    error("Esperado ')'");
                }
            }
        } else if (!match(TokenType.INTEIRO, null) && !match(TokenType.DECIMAL, null) && !match(TokenType.BOOLEAN, null) &&
                !match(TokenType.TRUE, null) && !match(TokenType.FALSE, null) && !match(TokenType.DELIMITADOR, "(") &&
                !match(TokenType.NOT, null)) {
            error("Fator inválido");
        }
    }

    private void lista_de_expressoes() {
        expressao();
        while (match(TokenType.DELIMITADOR, ",")) {
            expressao();
        }
    }
}

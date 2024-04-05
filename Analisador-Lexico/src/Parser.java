import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    public void programa() {
        if (currentTokenIndex < tokens.size()) {
            if (tokens.get(currentTokenIndex).getToken().equals("program")) {
                currentTokenIndex++;
                if (tokens.get(currentTokenIndex).getClassification().equals("IDENTIFIER")) {
                    currentTokenIndex++;
                    if (tokens.get(currentTokenIndex).getToken().equals(";")) {
                        currentTokenIndex++;
                        declaracoes_variaveis();
                        declaracoes_de_subprogramas();
                        comando_composto();
                        if (tokens.get(currentTokenIndex).getToken().equals(".")) {
                            System.out.println("Programa analisado com sucesso!");
                        } else {
                            System.out.println("Esperado '.' no final do programa");
                            System.out.print(tokens.get(currentTokenIndex).getLine() + " " + tokens.get(currentTokenIndex).getToken());
                        }
                    } else {
                        System.out.println("Esperado ';' após o nome do programa");
                        System.out.print(tokens.get(currentTokenIndex).getLine() + " " + tokens.get(currentTokenIndex).getToken());
                    }
                } else {
                    System.out.println("Esperado um identificador após 'program'");
                    System.out.print(tokens.get(currentTokenIndex).getLine() + " " + tokens.get(currentTokenIndex).getToken());
                }
            } else {
                System.out.println("Esperada a palavra-chave 'program'");
                System.out.print(tokens.get(currentTokenIndex).getLine() + " " + tokens.get(currentTokenIndex).getToken());
            }
        }
    }

    private void declaracoes_variaveis() {
        if (tokens.get(currentTokenIndex).getToken().equals("var")) {
            currentTokenIndex++;
            lista_declaracoes_variaveis();
        }
    }

    private void lista_declaracoes_variaveis() {
        if (tokens.get(currentTokenIndex).getClassification().equals("IDENTIFIER")) {
            lista_de_identificadores();
            if (tokens.get(currentTokenIndex).getToken().equals(":")) {
                currentTokenIndex++;
                tipo();
                if (tokens.get(currentTokenIndex).getToken().equals(";")) {
                    currentTokenIndex++;
                    lista_declaracoes_variaveis();
                } else {
                    System.out.println("Esperado ';' após o tipo");
                }
            } else {
                System.out.println("Esperado um tipo após os ':'");
            }
        }
    }

    private void lista_de_identificadores() {
        do {
            if (tokens.get(currentTokenIndex).getClassification().equals("IDENTIFIER")) {
                currentTokenIndex++;
                if (tokens.get(currentTokenIndex).getToken().equals(",")) {
                    currentTokenIndex++;
                } else {
                    break;
                }
            } else {
                System.out.println("Esperado um identificador");
            }
        } while (true);
    }

    private void declaracoes_de_subprogramas() {

        if (tokens.get(currentTokenIndex).getToken().equals("procedure")) {
            declaracao_de_subprograma();
        }
    }

    private void declaracao_de_subprograma() {
        if (tokens.get(currentTokenIndex).getToken().equals("procedure")) {
            currentTokenIndex++;
            if (tokens.get(currentTokenIndex).getClassification().equals("IDENTIFIER")) {
                currentTokenIndex++;
                argumentos();
                if (tokens.get(currentTokenIndex).getToken().equals(";")) {
                    currentTokenIndex++;
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                } else {
                    System.out.println("Esperado ';' após declaração de subprograma");
                }
            } else {
                System.out.println("Esperado identificador após 'procedure'");
            }
        } else {
            System.out.println("Esperada a palavra-chave 'procedure'");
        }
    }

    private void argumentos() {
        if (tokens.get(currentTokenIndex).getToken().equals("(")) {
            currentTokenIndex++;
            lista_de_parametros();
            if (tokens.get(currentTokenIndex).getToken().equals(")")) {
                currentTokenIndex++;
            } else {
                System.out.println("Esperado ')' após lista de parâmetros");
            }
        } else {
            System.out.println("Esperado '(' após 'procedure'");
        }
    }

    private void lista_de_parametros() {
        lista_de_identificadores();
        if (tokens.get(currentTokenIndex).getToken().equals(":")) {
            currentTokenIndex++;
            tipo();
            while (tokens.get(currentTokenIndex).getToken().equals(",")) {
                currentTokenIndex++;
                lista_de_identificadores();
                if (tokens.get(currentTokenIndex).getToken().equals(":")) {
                    currentTokenIndex++;
                    tipo();
                } else {
                    System.out.println("Esperado ':' após lista de identificadores");
                }
            }
        } else {
            System.out.println("Esperado ':' após lista de identificadores");
        }
    }

    private void tipo() {
        if (tokens.get(currentTokenIndex).getToken().equals("integer") ||
                tokens.get(currentTokenIndex).getToken().equals("real") ||
                tokens.get(currentTokenIndex).getToken().equals("boolean")) {
            currentTokenIndex++;
        } else {
            System.out.println("Esperado um tipo (integer, real, boolean)");
        }
    }

    private void comando_composto() {
        if (tokens.get(currentTokenIndex).getToken().equals("begin")) {
            currentTokenIndex++;
            comandos_opcionais();
            if (tokens.get(currentTokenIndex).getToken().equals("end")) {
                currentTokenIndex++;
            } else {
                System.out.println("Esperado 'end' após comandos opcionais");
            }
        } else {
            System.out.println("Esperado 'begin' para iniciar o comando composto");
        }
    }

    private void comandos_opcionais() {
        lista_de_comandos();
    }

    private void lista_de_comandos() {
        comando();
        while (tokens.get(currentTokenIndex).getToken().equals(";")) {
            currentTokenIndex++;
            if (tokens.get(currentTokenIndex).getToken().equals("end")) {
                break;
            } else {
                comando();
            }

        }
    }

    private void comando() {
        if (tokens.get(currentTokenIndex).getClassification().equals("IDENTIFIER")) {
            currentTokenIndex++;
            if (tokens.get(currentTokenIndex).getToken().equals(":=")) {
                currentTokenIndex++;
                expressao();
            }
        } else if (tokens.get(currentTokenIndex).getToken().equals("if")) {
            currentTokenIndex++;
            expressao();
            if (tokens.get(currentTokenIndex).getToken().equals("then")) {
                currentTokenIndex++;
                comando();
            } else {
                System.out.println("Esperado 'then' após expressão do if");
            }
        } else if (tokens.get(currentTokenIndex).getToken().equals("else")) {
            parte_else();

        } else if (tokens.get(currentTokenIndex).getToken().equals("while")) {
            currentTokenIndex++;
            expressao();
            if (tokens.get(currentTokenIndex).getToken().equals("do")) {
                currentTokenIndex++;
                comando();
            } else {
                System.out.println("Esperado 'do' após expressão do while");
            }

        } else if (tokens.get(currentTokenIndex).getToken().equals("begin")) {
            comando_composto();
        } else {
            System.out.print(tokens.get(currentTokenIndex).getLine() + " token: " + tokens.get(currentTokenIndex).getToken() + "\n");
            System.out.println("Comando inválido");
        }
    }

    private void parte_else() {
        if (tokens.get(currentTokenIndex).getToken().equals("else")) {
            currentTokenIndex++;
            comando();
        }
    }


    private void expressao() {
        expressao_simples();
        if (tokens.get(currentTokenIndex).getToken().equals("=") ||
                tokens.get(currentTokenIndex).getToken().equals("<") ||
                tokens.get(currentTokenIndex).getToken().equals(">") ||
                tokens.get(currentTokenIndex).getToken().equals("<=") ||
                tokens.get(currentTokenIndex).getToken().equals(">=") ||
                tokens.get(currentTokenIndex).getToken().equals("<>")) {
            currentTokenIndex++;
            expressao_simples();
        }
    }

    private void expressao_simples() {
        termo();
        while (tokens.get(currentTokenIndex).getToken().equals("+") ||
                tokens.get(currentTokenIndex).getToken().equals("-") ||
                tokens.get(currentTokenIndex).getToken().equals("or")) {
            currentTokenIndex++;
            termo();
        }
    }
    private void sinal(){
        if (tokens.get(currentTokenIndex).getToken().equals("-")||tokens.get(currentTokenIndex).getToken().equals("+")){
            currentTokenIndex++;
        }
    }

    private void termo() {
        fator();
        while (tokens.get(currentTokenIndex).getToken().equals("*") ||
                tokens.get(currentTokenIndex).getToken().equals("/") ||
                tokens.get(currentTokenIndex).getToken().equals("and")) {
            currentTokenIndex++;
            fator();
        }
    }

    private void fator() {
        sinal();
        if (tokens.get(currentTokenIndex).getClassification().equals("IDENTIFIER") ||
                tokens.get(currentTokenIndex).getClassification().equals("INTEGER") ||
                tokens.get(currentTokenIndex).getClassification().equals("REAL") ||
                tokens.get(currentTokenIndex).getToken().equals("true") ||
                tokens.get(currentTokenIndex).getToken().equals("false")) {

            currentTokenIndex++;
        } else if (tokens.get(currentTokenIndex).getToken().equals("(")) {
            currentTokenIndex++;
            expressao();
            if (tokens.get(currentTokenIndex).getToken().equals(")")) {
                currentTokenIndex++;
            } else {
                System.out.println("Esperado ')' após expressão");
            }
        } else if (tokens.get(currentTokenIndex).getToken().equals("not")) {
            currentTokenIndex++;
            fator();
        }
        else {
            System.out.println("esse token esta dando erro: " + tokens.get(currentTokenIndex).getToken() + " | linha " + tokens.get(currentTokenIndex).getLine());
            System.out.println("Fator inválido");
        }
    }
}

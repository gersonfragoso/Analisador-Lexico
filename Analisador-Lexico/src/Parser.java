import Erro.ErroIdentifierExcepition;

import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int currentTokenIndex;
    private Stack<String> variaveis;
    private Map<String, String> tiposVar;
    private Map<String, String> tiposVarEscopo;
    private List<String> operacao;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.variaveis = new Stack<>();
        this.tiposVar = new LinkedHashMap<>();
        this.tiposVarEscopo = new LinkedHashMap<>();
        this.operacao = new ArrayList<>();
    }

    public boolean varExiste(String var){
        for (String variavei : variaveis) {
            if (Objects.equals(var, variavei)) {
                return false;
            }
        }
        return true;
    }

    public void logicaAddVarClass(Stack<String> variaveis) {
        String tipoAtual = null;

        while (!variaveis.isEmpty()) {
            String var = variaveis.pop();

            if (var.equals("mark")) {
                // Se encontrar a marca "mark", muda para o próximo tipo
                tipoAtual = variaveis.pop();
            } else {
                // Associa a variável ao tipo atual
                tiposVar.put(var, tipoAtual);
            }
        }
        System.out.println("Var escopo global:");

        for (Map.Entry<String, String> entry : tiposVar.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }
    public void logicaAddVarClassEscopo(Stack<String> variaveis) {
        String tipoAtual = null;

        while (!variaveis.isEmpty()) {
            String var = variaveis.pop();

            if (var.equals("mark")) {
                // Se encontrar a marca "mark", muda para o próximo tipo
                tipoAtual = variaveis.pop();
            } else {
                // Associa a variável ao tipo atual
                tiposVarEscopo.put(var, tipoAtual);
            }
        }
        System.out.println("Procedure:");

        for (Map.Entry<String, String> entry : tiposVarEscopo.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }
    public void verificar (List<String>operacao){
        operacao.add("mark");
        operacao.remove(0);
        List<String> verificaAtual = new ArrayList<>();
        while (true){
            for (int i = 0; !Objects.equals(operacao.get(i), "mark"); i++){
                String atual = operacao.get(i);
                if (tiposVar.containsKey(atual)){
                    for (Map.Entry<String, String> entry : tiposVar.entrySet()) {
                        if (atual.equals(entry.getKey())) {
                            verificaAtual.add(entry.getValue());
                            operacao.remove(i);
                            System.out.println(operacao);
                            System.out.println(verificaAtual);
                        }
                    }
                } else if (atual.equals(":=")) {
                    operacao.remove(i);
                }else if (atual.equals("*")){
                    operacao.remove(i);
                }
                if (atual.equals("mark")){
                    if (verificaAtual.get(0).equals("real")){
                        for (int j = 1; j < verificaAtual.size(); j++){
                            if (verificaAtual.get(i).equals("boolean"))   {
                                System.out.println("Erro variavel boolean declarada em lugar errado");
                            }else {
                                System.out.println("Ok");
                            }
                        }
                    }else if (verificaAtual.get(0).equals("integer")) {
                        for (int j = 1; j < verificaAtual.size(); j++) {
                            if (verificaAtual.get(i).equals("boolean") || verificaAtual.get(i).equals("real")) {
                                System.out.println("Erro variavel boolean|real declarada em lugar errado");
                            } else {
                                System.out.println("Ok");
                            }
                        }
                    } else if (verificaAtual.get(0).equals("boolean")) {
                        for (int j = 1; j < verificaAtual.size(); j++) {
                            if (verificaAtual.get(i).equals("integer") || verificaAtual.get(i).equals("real")) {
                                System.out.println("Erro variavel real|integer declarada em lugar errado");
                            } else {
                                System.out.println("Ok");
                            }
                        }

                    }else if (operacao.get(i) == "mark"){
                       operacao.remove(i);
                    }
                }
                if (operacao.isEmpty()){
                    break;
                }


            }

        }
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
                        logicaAddVarClass(variaveis);
                        declaracoes_de_subprogramas();
                        logicaAddVarClassEscopo(variaveis);
                        comando_composto();
                        System.out.println(operacao);

                        //verificar(operacao);
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
                String TokenAtual = tokens.get(currentTokenIndex).getToken();
                if (varExiste(TokenAtual)){
                    variaveis.add(tokens.get(currentTokenIndex).getToken());
                    currentTokenIndex++;
                }else {
                    try {
                        throw new ErroIdentifierExcepition("Ja existe uma variavel com esses nome");
                    } catch (ErroIdentifierExcepition e) {
                        throw new RuntimeException("variavel: "+ tokens.get(currentTokenIndex).getToken());
                    }
                }
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
            variaveis.add(tokens.get(currentTokenIndex).getToken());
            variaveis.add("mark");
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
            operacao.add("mark");
            operacao.add(tokens.get(currentTokenIndex).getToken());
            currentTokenIndex++;
            if (tokens.get(currentTokenIndex).getToken().equals(":=")) {
                operacao.add(tokens.get(currentTokenIndex).getToken());
                currentTokenIndex++;
                expressao();
            }
        } else if (tokens.get(currentTokenIndex).getToken().equals("if")) {
            operacao.add("mark");
            currentTokenIndex++;
            expressao();
            if (tokens.get(currentTokenIndex).getToken().equals("then")) {
                currentTokenIndex++;
                comando();
                if (tokens.get(currentTokenIndex).getToken().equals("else")){
                    parte_else();
                }
            } else {
                System.out.println("Esperado 'then' após expressão do if");
            }


        } else if (tokens.get(currentTokenIndex).getToken().equals("while")) {
            currentTokenIndex++;
            operacao.add("mark");
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
            operacao.add(tokens.get(currentTokenIndex).getToken());

            currentTokenIndex++;
            expressao_simples();
        }
    }

    private void expressao_simples() {
        termo();
        while (tokens.get(currentTokenIndex).getToken().equals("+") ||
                tokens.get(currentTokenIndex).getToken().equals("-") ||
                tokens.get(currentTokenIndex).getToken().equals("or")) {
            operacao.add(tokens.get(currentTokenIndex).getToken());
            currentTokenIndex++;
            termo();
        }
    }
    private void sinal(){
        if (tokens.get(currentTokenIndex).getToken().equals("-")||tokens.get(currentTokenIndex).getToken().equals("+")){
            operacao.add(tokens.get(currentTokenIndex).getToken());
            currentTokenIndex++;
        }
    }

    private void termo() {
        fator();
        while (tokens.get(currentTokenIndex).getToken().equals("*") ||
                tokens.get(currentTokenIndex).getToken().equals("/") ||
                tokens.get(currentTokenIndex).getToken().equals("and")) {
            operacao.add(tokens.get(currentTokenIndex).getToken());

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
            operacao.add(tokens.get(currentTokenIndex).getToken());

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

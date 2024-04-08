# Projeto-Compiladores

Este é um projeto da matéria de CONSTRUÇÃO DE COMPILADORES I, onde foi proposto criar um compilador da linguagem Pascal.

### Autores: [Gerson Fragoso](https://github.com/gersonfragoso), [Luiz Eduardo](https://github.com/Luiz-Eduardo-DS)

Aqui vai uma pequena explicação sobre o que nosso código faz e a lógica que implementamos:

## Lexer

- Responsável por tokenizar a entrada e separar por categorias que são:
  - **KEYWORD**: Palavras-chave para determinadas ações: (program|var|integer|real|boolean|procedure|begin|end|if|then|else|while|do|not)
  - **BOOLEAN**: Classificador do tipo TRUE, FALSE
  - **REAL**: Classificador dos números de ponto flutuante e também de variáveis
  - **INTEGER**: Classificador dos números inteiros e também de variáveis
  - **ASSIGNMENT**: Atribuidor representado por ":=" onde você estará atribuindo um valor/expressão a uma variável
  - **RELATIONAL**: Operações relacionais como Maior que, Menor que, Maior ou igual, etc.
  - **ADDITIVE**: Classificador das operações básicas: Mais, Menos e Or
  - **MULTIPLICATION**: Classificador para as operações de Multiplicação, Divisão e And
  - **IDENTIFIER**: Classificador de identificação, representando nomes de variáveis, programas ou procedimentos
  - **DELIMITER**: Classificador de limitadores: ",", ".", ";", ":", "(", ")"
  - **ERROR**: Classificador para erros desconhecidos ao tokenizar
  - **COMMENT**: Identificação de comentários entre **{ }**, ignorados pelo tokenizador

## Parser

Este módulo é responsável por seguir a lógica sintática e garantir que a entrada não contenha erros sintáticos.

- **programa** →
  program id;
  declarações_variáveis
  declarações_de_subprogramas
  comando_composto
  .

- **declarações_variáveis** →
  var lista_declarações_variáveis
  | ε

- **lista_declarações_variáveis** →
  lista_declarações_variáveis lista_de_identificadores: tipo;
  | lista_de_identificadores: tipo;

- **lista_de_identificadores** →
  id
  | lista_de_identificadores, id

- **tipo** →
  integer
  | real
  | boolean

- **declarações_de_subprogramas** →
  declarações_de_subprogramas declaração_de_subprograma;
  | ε

- **declaração_de_subprograma** →
  procedure id argumentos;
  declarações_variáveis
  declarações_de_subprogramas
  comando_composto

- **argumentos** →
  (lista_de_parametros)
  | ε

- **lista_de_parametros** →
  lista_de_identificadores: tipo
  | lista_de_parametros; lista_de_identificadores: tipo

- **comando_composto** →
  begin
  comandos_opcionais
  end

- **comandos_opcionais** →
  lista_de_comandos
  | ε

- **lista_de_comandos** →
  comando
  | lista_de_comandos; comando

- **comando** →
  variável := expressão
  | ativação_de_procedimento
  | comando_composto
  | if expressão then comando parte_else
  | while expressão do comando

- **parte_else** →
  else comando
  | ε

- **variável** →
  id

- **ativação_de_procedimento** →
  id
  | id (lista_de_expressões)

- **lista_de_expressões** →
  expressão
  | lista_de_expressões, expressão

- **expressão** →
  expressão_simples
  | expressão_simples op_relacional expressão_simples

- **expressão_simples** →
  termo
  | sinal termo
  | expressão_simples op_aditivo termo

- **termo** →
  fator
  | termo op_multiplicativo fator

- **fator** →
  id
  | id(lista_de_expressões)
  | num_int
  | num_real
  | true
  | false
  | (expressão)
  | not fator

- **sinal** →
  '+'
  | '-'

- **op_relacional** →
  '='
  | <
  | >
  | <=
  | >=
  | <>

- **op_aditivo** →
  '+'
  | '-'
  | or

- **op_multiplicativo** →
  *
  | /
  | and

## Token

- A classe Token auxilia em ambos compiladores, tanto o Léxico quanto o Sintático.
- Cria um objeto Token com 3 características:
  - Token atual, a string que aquele token representa
  - Classificação do token, o tipo de Keyword que ele é
  - Linha do token, para facilitar o debug

## Main

- Lê o caminho do teste da gramática e chama os compiladores lexer e parser.

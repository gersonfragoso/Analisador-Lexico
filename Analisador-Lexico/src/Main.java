import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath = "src/arquivosTeste/Test3.txt";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder input = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                input.append(line).append('\n');
            }
            reader.close();

            // Realiza a análise léxica
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.match(String.valueOf(input));
            // Exibe os tokens em formato de tabela
            System.out.println("\nTokens:");
            System.out.println(String.format("%-20s %-20s %-10s", "Token", "Tipo", "Linha"));
            System.out.println("".repeat(50));
            for (Token token : tokens) {
                System.out.println(String.format("%-20s %-20s %-10s",
                        token.getToken(), token.getClassification(), token.getLine()));
            }

            // Realiza a análise sintática
            Parser parser = new Parser(tokens);
            parser.programa();


        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}

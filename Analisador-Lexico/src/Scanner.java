


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner {
    public static void main(String[] args) {
        try {
            String filePath = "src/gramatica.txt";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder input = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                input.append(line).append('\n');
            }
            reader.close();
            Lexer lexer = new Lexer(input.toString());
            ArrayList<Token> tokens = lexer.tokenize();

            // Exibir os tokens em formato de tabela
            System.out.println("\nTokens:");
            System.out.println(String.format("%-20s %-20s %-10s", "Token", "Tipo", "Linha"));
            System.out.println("".repeat(50)); // Linha horizontal
            for (Token token : tokens) {
                System.out.println(String.format("%-20s %-20s %-10s",
                        token.getValue(), token.getType(), token.getLine()));
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}

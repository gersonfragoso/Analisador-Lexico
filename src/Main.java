import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Lê o conteúdo do arquivo "entrada.txt"
            Scanner arquivoScanner = new Scanner(new File("entrada.txt"));
            StringBuilder codigo = new StringBuilder();

            while (arquivoScanner.hasNextLine()) {
                codigo.append(arquivoScanner.nextLine()).append("\n");
            }

            arquivoScanner.close();

            // Cria um compilador e tokeniza o código
            Compilador compilador = new Compilador(codigo.toString());
            compilador.inicializarPalavrasReservadas();
            compilador.tokenize();

            // Aqui você pode fazer algo com os tokens, se necessário

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo 'entrada.txt' não encontrado.");
        }
    }
}

package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Main <directory-to-index>");
            return;
        }

        Path folder = Path.of(args[0]);
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.indexDirectory(folder);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Indexed " + folder + ". Enter a query (or 'quit' to exit):");

        String query = bufferedReader.readLine();
        while (query != null && !query.equals("quit")) {
            try {
                for (Document document : searchEngine.search(query)) {
                    System.out.println(document);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid query: " + e.getMessage());
            }
            query = bufferedReader.readLine();
        }
    }
}

package main.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchEngine {
    private int docId = 0;

    private final Map<Integer, Document> documents = new HashMap<>();
    private final main.java.Index index = new main.java.Index();
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngine.class);

    private List<Path> loadDirectory(Path folder) throws IOException {
        try (Stream<Path> paths = Files.list(folder)) {
            return paths.filter(Files::isRegularFile).toList();
        }
    }

    private void index(List<Path> files) {
        for (Path file : files) {
            documents.put(docId, new Document(docId, file.getFileName().toString(), file));
            String fileContent = "";
            try {
                fileContent = Files.readString(file);
            } catch (IOException e) {
                LOGGER.error("Error reading file: " + file, e);
            }
            main.java.Tokenizer tokenizer = new main.java.Tokenizer();
            List<String> tokens = tokenizer.tokenize(fileContent);
            for (int i = 0; i < tokens.size(); i++) {
                String term = tokens.get(i);
                index.addTerm(term, docId, i);
            }
            docId++;
        }
    }
}

package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class SearchEngine {
    private int docId = 0;

    private final Map<Integer, Document> documents = new HashMap<>();
    private final Index index = new Index();
    private final Tokenizer tokenizer = new Tokenizer();

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngine.class);

    public void indexDirectory(Path folder) throws IOException {
        List<Path> files = loadDirectory(folder);
        index(files);
    }

    public Set<Document> search(String query) {
        List<String> tokens = tokenizer.tokenize(query);
        Set<Document> results = new HashSet<>();
        for (String token : tokens) {
            for (int docId : index.getDocuments(token)) {
                results.add(documents.get(docId));
            }
        }
        return results;
    }

    private List<Path> loadDirectory(Path folder) throws IOException {
        try (Stream<Path> paths = Files.list(folder)) {
            return paths.filter(Files::isRegularFile).toList();
        }
    }

    private void index(List<Path> files) {
        for (Path file : files) {
            String fileContent = "";
            try {
                fileContent = Files.readString(file);
            } catch (IOException e) {
                LOGGER.error("Error reading file: " + file, e);
                continue;
            }
            List<String> tokens = tokenizer.tokenize(fileContent);
            for (int i = 0; i < tokens.size(); i++) {
                String term = tokens.get(i);
                index.addTerm(term, docId, i);
            }
            documents.put(docId, new Document(docId++, file.getFileName().toString(), file));
        }
    }
}

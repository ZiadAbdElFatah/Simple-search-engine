package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/// SearchEngine class that provides methods to index documents and search for terms in the indexed documents.
///
/// It uses the [Tokenizer] class to tokenize input strings, the [Index] class to maintain an inverted index of terms and their corresponding postings, and the [Document] class to represent documents in the search engine.
public class SearchEngine {
    private int docId = 0;

    private final Map<Integer, Document> documents = new HashMap<>();
    private final Index index = new Index();
    private final Tokenizer tokenizer = new Tokenizer();

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngine.class);

    /// Indexes all the files in the given folder and its subfolders.
    public void indexDirectory(Path folder) throws IOException {
        List<Path> files = loadDirectory(folder);
        index(files);
    }

    /// Searches for documents that contain the given query string.
    ///
    /// @param query the query string to search for
    /// @return a list of documents that contain the query string sorted according to TF-IDF
    public List<Document> search(String query) {
        List<String> tokens = tokenizer.tokenize(query);
        TfIdfScorer scorer = new TfIdfScorer(index, documents.size());
        Map<Integer, Double> scores = scorer.score(tokens);

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(entry -> documents.get(entry.getKey()))
                .toList();
    }

    private List<Path> loadDirectory(Path folder) throws IOException {
        try (Stream<Path> paths = Files.list(folder)) {
            return paths.filter(Files::isRegularFile).toList();
        }
    }

    /// Indexes the given list of files by reading their content, tokenizing it, adding the terms to the inverted index, and auto incrementing the document ID.
    ///
    /// If a file cannot be read, it logs an error and continues with the next file.
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

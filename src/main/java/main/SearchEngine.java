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
    private final List<TextExtractor> extractors = List.of(new PdfTextExtractor(), new PlainTextExtractor());
    private final QueryParser queryParser = new QueryParser();

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngine.class);

    /// Indexes all the files in the given folder and its subfolders.
    public void indexDirectory(Path folder) throws IOException {
        List<Path> files = loadDirectory(folder);
        index(files);
    }

    /// Supports AND / OR / NOT (precedence: NOT, then AND, then OR), "quoted phrases",
    /// and implicit OR between adjacent terms. Results are ranked by TF-IDF.
    ///
    /// @throws IllegalArgumentException if the query is malformed (e.g. "java AND")
    public List<Document> search(String query) {
        QueryNode tree = new QueryTreeBuilder().build(queryParser.parse(query));
        if (tree == null) {
            return List.of();
        }

        List<Integer> matches = new QueryEvaluator(index, documents.size()).evaluate(tree);

        List<String> terms = new ArrayList<>();
        collectPositiveTerms(tree, terms);
        Map<Integer, Double> scores = new TfIdfScorer(index, documents.size())
                .score(terms.stream().distinct().toList());

        return matches.stream()
                .sorted(Comparator.comparingDouble((Integer id) -> scores.getOrDefault(id, 0.0)).reversed())
                .map(documents::get)
                .toList();
    }

    /// Terms under NOT don't contribute to ranking: they describe what a good result lacks.
    private void collectPositiveTerms(QueryNode node, List<String> out) {
        switch (node) {
            case QueryNode.TermNode t -> out.add(t.term());
            case QueryNode.PhraseNode p -> out.addAll(p.words());
            case QueryNode.AndNode a -> {
                collectPositiveTerms(a.left(), out);
                collectPositiveTerms(a.right(), out);
            }
            case QueryNode.OrNode o -> {
                collectPositiveTerms(o.left(), out);
                collectPositiveTerms(o.right(), out);
            }
            case QueryNode.NotNode _ -> { }
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
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
            TextExtractor extractor = findExecutor(file);
            if (extractor == null) {
                continue;
            }
            String fileContent;
            try {
                fileContent = extractor.extractText(file);
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

    private TextExtractor findExecutor(Path file) {
        for (TextExtractor extractor : extractors) {
            if (extractor.supports(file)) {
                return extractor;
            }
        }
        LOGGER.error("File type not supported");
        return null;
    }
}

package main;

import java.nio.file.Path;
import java.util.Objects;

/// Document class that represents a document in the search engine.
///
/// A document consists of a unique document ID, a file name, and a file path to let users access the documents in the search results.
public class Document {
    private final int docId;
    private final String fileName;
    private final Path filePath;

    public Document(int id, String fileName, Path filePath) {
        this.docId = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document other)) return false;
        // docId is enough as it's unique for each Document
        return docId == other.docId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId);
    }

    @Override
    public String toString() {
        return fileName + " (" + filePath + ")";
    }
}

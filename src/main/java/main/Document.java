package main;

import java.nio.file.Path;
import java.util.Objects;

public class Document {
    private final int docId;
    private final String fileName;
    private final Path filePath;

    public Document(int id, String fileName, Path filePath) {
        this.docId = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public int getDocId() {
        return docId;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getFilePath() {
        return filePath;
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
}

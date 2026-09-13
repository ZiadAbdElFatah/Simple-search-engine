package main;

import java.nio.file.Path;

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
}

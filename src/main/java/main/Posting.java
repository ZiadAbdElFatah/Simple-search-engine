package main;

import java.util.List;

/// Posting class that represents a posting in the inverted index.
///
/// A posting consists of a document ID and a list of positions where the term appears in that document(gives the ability to get the term frequency).
public class Posting {
    private final int docId;
    private final List<Integer> positions;

    public Posting(int docId, List<Integer> positions) {
        this.docId = docId;
        this.positions = positions;
    }

    public int getDocId() {
        return docId;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public int getTermFrequency() {
        return positions.size();
    }
}

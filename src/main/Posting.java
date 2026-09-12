package main;

import java.util.List;

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

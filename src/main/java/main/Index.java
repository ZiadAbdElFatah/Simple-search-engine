package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {
    private final Map<String, List<Posting>> postings = new HashMap<>();

    public void addTerm(String term, int docId, int position) {
        List<Posting> termPostings = postings.computeIfAbsent(term, _ -> new ArrayList<>());
        if (termPostings.isEmpty() || termPostings.getLast().getDocId() != docId) {
            termPostings.add(new Posting(docId, new ArrayList<>()));
        }
        termPostings.getLast().getPositions().add(position);
    }

    public List<Posting> getPostings(String term) {
        List<Posting> termPostings = postings.get(term);
        if (termPostings == null) {
            return new ArrayList<>();
        }
        return termPostings;
    }

    public List<Integer> getDocuments(String token) {
        return getPostings(token).stream()
                .map(Posting::getDocId)
                .toList();
    }
}

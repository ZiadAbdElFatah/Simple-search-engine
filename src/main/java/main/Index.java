package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Index class that represents an inverted index for a collection of documents.
///
/// The index maps terms to a list of [Posting].
public class Index {
    private final Map<String, List<Posting>> postings = new HashMap<>();

    public void addTerm(String term, int docId, int position) {
        List<Posting> termPostings = postings.computeIfAbsent(term, _ -> new ArrayList<>());
        // If the last posting in the list is for a different document, create a new posting for the current document.
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

    /// @return a list of document IDs that contain the given term.
    public List<Integer> getDocuments(String token) {
        return getPostings(token).stream()
                .map(Posting::getDocId)
                .toList();
    }
}

package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// TfIdfScorer class that calculates the TF-IDF score for a list of query terms against an inverted index to help sorting the search results.
public class TfIdfScorer {
    private final Index index;
    private final int totalDocuments;

    public TfIdfScorer(Index index, int totalDocuments) {
        this.index = index;
        this.totalDocuments = totalDocuments;
    }

    /// Calculates the TF-IDF score for each document that contains the given query terms.
    ///
    /// @param queryTerms the list of query terms to score
    /// @return a map of document IDs to their corresponding TF-IDF scores
    public Map<Integer, Double> score(List<String> queryTerms) {
        Map<Integer, Double> scores = new HashMap<>();
        for (String term : queryTerms) {
            List<Posting> postings = index.getPostings(term);
            double idfScore = computeIdf(postings.size());
            for (Posting posting : postings) {
                int tfScore = posting.getTermFrequency();
                double totalScore = tfScore * idfScore;
                scores.merge(posting.getDocId(), totalScore, Double::sum);
            }
        }
        return scores;
    }

    /// Helper method to compute the Inverse Document Frequency (IDF) score for a term based on its document frequency.
    private double computeIdf(int documentFrequency) {
        if (documentFrequency == 0) {
            return 0;
        }
        return Math.log((double) totalDocuments / documentFrequency);
    }
}

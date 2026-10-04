package main;

import main.QueryNode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class QueryEvaluator {
    private final Index index;
    private final List<Integer> allDocIds; // docIds are contiguous 0..n-1, so this is sorted

    public QueryEvaluator(Index index, int totalDocuments) {
        this.index = index;
        this.allDocIds = IntStream.range(0, totalDocuments).boxed().toList();
    }

    /// Returns the matching docIds, sorted ascending.
    public List<Integer> evaluate(QueryNode node) {
        return switch (node) {
            case TermNode t -> index.getDocuments(t.term());
            case PhraseNode p -> phraseMatches(p.words());
            case AndNode a -> intersect(evaluate(a.left()), evaluate(a.right()));
            case OrNode o -> union(evaluate(o.left()), evaluate(o.right()));
            case NotNode n -> difference(allDocIds, evaluate(n.child()));
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    static List<Integer> intersect(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            int x = a.get(i), y = b.get(j);
            if (x == y) {
                result.add(x);
                i++;
                j++;
            } else if (x < y) {
                i++;
            } else {
                j++;
            }
        }
        return result;
    }

    static List<Integer> union(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            int x = a.get(i), y = b.get(j);
            if (x == y) {
                result.add(x);
                i++;
                j++;
            } else if (x < y) {
                result.add(x);
                i++;
            } else {
                result.add(y);
                j++;
            }
        }
        while (i < a.size()) result.add(a.get(i++));
        while (j < b.size()) result.add(b.get(j++));
        return result;
    }

    static List<Integer> difference(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>();
        int i = 0;
        for (int x : a) {
            while (i < b.size() && b.get(i) < x) i++;
            boolean inB = i < b.size() && b.get(i) == x;
            if (!inB) result.add(x);
        }
        return result;
    }

    /// Phrase search: walk all the words' postings lists in lockstep on docId,
    /// then check positions p, p+1, p+2... inside each document where all words occur.
    private List<Integer> phraseMatches(List<String> words) {
        List<List<Posting>> lists = new ArrayList<>();
        for (String word : words) {
            List<Posting> postings = index.getPostings(word);
            if (postings.isEmpty()) return List.of();
            lists.add(postings);
        }

        int[] ptr = new int[lists.size()];
        List<Integer> result = new ArrayList<>();
        while (true) {
            int maxDoc = -1;
            for (int i = 0; i < lists.size(); i++) {
                if (ptr[i] >= lists.get(i).size()) return result;
                maxDoc = Math.max(maxDoc, lists.get(i).get(ptr[i]).getDocId());
            }
            boolean aligned = true;
            for (int i = 0; i < lists.size(); i++) {
                List<Posting> list = lists.get(i);
                while (ptr[i] < list.size() && list.get(ptr[i]).getDocId() < maxDoc) ptr[i]++;
                if (ptr[i] >= list.size()) return result;
                if (list.get(ptr[i]).getDocId() != maxDoc) aligned = false;
            }
            if (aligned) {
                if (hasConsecutivePositions(lists, ptr)) result.add(maxDoc);
                for (int k = 0; k < ptr.length; k++) ptr[k]++;
            }
        }
    }

    private boolean hasConsecutivePositions(List<List<Posting>> lists, int[] ptr) {
        for (int start : lists.getFirst().get(ptr[0]).getPositions()) {
            boolean matches = true;
            for (int k = 1; k < lists.size() && matches; k++) {
                List<Integer> positions = lists.get(k).get(ptr[k]).getPositions();
                matches = Collections.binarySearch(positions, start + k) >= 0;
            }
            if (matches) return true;
        }
        return false;
    }
}
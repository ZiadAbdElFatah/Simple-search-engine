package main;

import java.util.List;

/// QueryNode interface that represents a node in a query tree.
///
/// It has several implementations: TermNode, PhraseNode, AndNode, OrNode, and NotNode. Each implementation represents a different type of query operation or term.
public interface QueryNode {
    record TermNode(String term) implements QueryNode{}
    record PhraseNode(List<String> words) implements QueryNode{}
    record AndNode(QueryNode left, QueryNode right) implements QueryNode{}
    record OrNode(QueryNode left, QueryNode right) implements QueryNode{}
    record NotNode(QueryNode child) implements QueryNode{}
}

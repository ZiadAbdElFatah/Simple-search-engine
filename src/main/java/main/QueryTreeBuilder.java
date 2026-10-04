package main;

import main.QueryNode.*;

import java.util.List;

/// QueryTreeBuilder class that builds a query tree from a list of QueryElements.
///
/// It uses a recursive descent parser to parse the list of QueryElements and construct a tree of QueryNodes that represents the query.
public class QueryTreeBuilder {
    private List<QueryElement> elements;
    private int pos;

    /// Returns null for an empty query. Throws IllegalArgumentException for malformed ones.
    public QueryNode build(List<QueryElement> elements) {
        this.elements = elements;
        this.pos = 0;
        if (elements.isEmpty()) {
            return null;
        }
        return parseOr();
    }

    /// Parses an OR expression, which consists of one or more AND expressions separated by OR operators.
    private QueryNode parseOr() {
        QueryNode left = parseAnd();
        while (peekIsOperator(OperatorType.OR) || peekIsOperand()) {
            if (peekIsOperator(OperatorType.OR)) {
                pos++;
            }
            left = new OrNode(left, parseAnd());
        }
        return left;
    }

    /// Parses an AND expression, which consists of one or more unary expressions separated by AND operators.
    /// It also handles binary NOT expressions, which are treated as "AND NOT" operations
    private QueryNode parseAnd() {
        QueryNode left = parseUnary();
        while (peekIsOperator(OperatorType.AND) || peekIsOperator(OperatorType.NOT)) {
            if (peekIsOperator(OperatorType.AND)) {
                pos++;
                left = new AndNode(left, parseUnary());
            } else {
                pos++; // binary NOT: "a NOT b" == "a AND NOT b"
                left = new AndNode(left, new NotNode(parseUnary()));
            }
        }
        return left;
    }

    /// Parses a unary expression, which can be a NOT operation followed by another unary expression, or a primary expression (term or phrase).
    /// If a NOT operator is found, it creates a NotNode with the result of parsing the next unary expression. If no NOT operator is found, it parses a primary expression.
    private QueryNode parseUnary() {
        if (peekIsOperator(OperatorType.NOT)) {
            pos++;
            return new NotNode(parseUnary());
        }
        return parsePrimary();
    }

    /// Parses a primary expression, which can be a term or a phrase.
    private QueryNode parsePrimary() {
        QueryElement element = peek();
        if (element == null) {
            throw new IllegalArgumentException("Query ended unexpectedly");
        }
        pos++;
        return switch (element) {
            case Term t -> new TermNode(t.value());
            case Phrase p -> new PhraseNode(p.words());
            case Operator op -> throw new IllegalArgumentException("Unexpected operator: " + op.type());
        };
    }

    /// Peeks at the next QueryElement in the list without advancing the position.
    ///
    /// @return the next QueryElement, or null if there are no more elements
    private QueryElement peek() {
        if (pos < elements.size()) {
            return elements.get(pos);
        }
        return null;
    }

    /// Checks if the next QueryElement is an operator of the specified type.
    ///
    /// @param type the type of operator to check for
    /// @return true if the next QueryElement is an operator of the specified type, false otherwise
    private boolean peekIsOperator(OperatorType type) {
        if (peek() instanceof Operator op && op.type() == type) {
            return true;
        }
        return false;
    }

    /// Checks if the next QueryElement is an operand (term or phrase).
    private boolean peekIsOperand() {
        QueryElement element = peek();
        if (element instanceof Term || element instanceof Phrase) {
            return true;
        }
        return false;
    }
}
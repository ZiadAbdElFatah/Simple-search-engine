package main;

public sealed interface QueryElement permits Term, Phrase, Operator {}


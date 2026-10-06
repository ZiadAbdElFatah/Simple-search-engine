# Document Search Engine

This is a text search engine I built from scratch in Java no Lucene, no existing search library, just the actual data structures and algorithms that make search engines work: an inverted index, Term Frequency(TF)-Inverse Document Frequency(IDF) ranking, a boolean query parser, and phrase search.

I built this mainly to understand information retrieval at the level of "how does this actually work," not just "how do I call a search API." I'd already used Lucene as a black box in my JabRef OCR work, and I wanted to understand what was happening underneath the hood.

## What it can do

- Index a folder of `.txt` or `.pdf` files and search across them
- Rank results by relevance (TF-IDF), not just by whether they match
- Handle real boolean query syntax: `AND`, `OR`, `NOT`, with the precedence you'd expect (`a AND b OR c` means `(a AND b) OR c`, the same as most query languages)
- Search for exact phrases with `"quoted text"` not just "these words appear somewhere," but "these words appear next to each other, in this order"
- Fall back gracefully when a file can't be read or parsed, instead of crashing the whole run

## How it's put together

The core idea is an **inverted index**: instead of storing "document -> words it contains," you store "word -> documents that contain it, and exactly where." That reversal is what makes search fast a query for "java" goes straight to java's list of documents, instead of scanning every document in the corpus.

Each piece has one job:

- **`Tokenizer`** turns raw text into normalized words lowercased, hyphens split into separate words, apostrophes stripped (`don't` -> `dont`), but stopwords like "the" and "a" are deliberately *kept*, because phrase search needs them (more on that below).
- **`Index`** is the actual inverted index for every term, a list of postings (which document, and every position in that document where the word occurs).
- **`TextExtractor`** is a small interface with two implementations, one for plain text and one for PDF (via Apache PDFBox), so the rest of the system never has to know or care what format a document came from.
- **`TfIdfScorer`** computes relevance scores, so results aren't just "matched or didn't" they're ranked.
- **`QueryParser` -> `QueryTreeBuilder` -> `QueryEvaluator`** is the query pipeline: parse the raw query string into typed pieces (term, phrase, operator), build a tree that respects operator precedence, then evaluate that tree into actual matching documents using set intersection/union/difference over the index's postings lists.

## Running it
`./gradlew run --args="/path/to/your/documents"`
Then just type queries that you want to search, and type "quit" to exit the application.

## Benchmark

I indexed 100,000 full Wikipedia articles (682MB of text, via the [`wikimedia/wikipedia`](https://huggingface.co/datasets/wikimedia/wikipedia) dataset) to get a real number instead of guessing:

- **Indexing**: 24.0 seconds (~4,165 docs/sec, ~28.4 MB/sec)
- **Query latency**: 6.46ms average, over 300 queries across 6 different query shapes (including boolean and phrase queries).

## If I come back to this

- BM25 ranking
- OCR fallback for the PDF font-encoding issue above

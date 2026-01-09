import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WikiSearch {

    private static final String INDEX_PATH =
            "/Volumes/T7 Shield/wiki_pipeline/index/simplewiki_lucene";

    public static List<SearchResult> search(String question, int maxHits) {
        List<SearchResult> results = new ArrayList<>();

        try (var dir = FSDirectory.open(Path.of(INDEX_PATH));
             var reader = DirectoryReader.open(dir)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();

            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(QueryParser.escape(question));

            TopDocs hits = searcher.search(query, maxHits);

            for (ScoreDoc sd : hits.scoreDocs) {
                Document doc = searcher.storedFields().document(sd.doc);

                String title = safe(doc.get("title"));
                String content = safe(doc.get("content"));

                if (title.isBlank() && content.isBlank()) continue;

                String snippet = makeSnippet(cleanWikiText(content));

                results.add(new SearchResult(
                        title.isBlank() ? "(Untitled)" : title,
                        snippet.isBlank() ? "No readable content found." : snippet,
                        "OFFLINE",
                        null,
                        null
                ));
            }

            if (results.isEmpty()) {
                results.add(new SearchResult(
                        "No results",
                        "No relevant information found.",
                        "OFFLINE",
                        null,
                        null
                ));
            }

            return results;

        } catch (Exception e) {
            return List.of(new SearchResult(
                    "Search error",
                    "Search error: " + e.getMessage(),
                    "OFFLINE",
                    null,
                    null
            ));
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String makeSnippet(String text) {
        if (text == null) return "";
        String t = text.trim();
        if (t.length() <= 600) return t;
        return t.substring(0, 600) + "...";
    }

    // Lightweight cleanup: removes the most common wiki artifacts that leak into plaintext dumps
    private static String cleanWikiText(String text) {
        if (text == null) return "";

        String t = text;

        // Remove [[File:...]] blocks (common source of 200px|thumb|right)
        t = t.replaceAll("\\[\\[(File|Image):[^]]+]]", "");

        // Remove templates {{...}} (best-effort, not perfect)
        t = t.replaceAll("\\{\\{[^}]+}}", "");

        // Remove Category: lines
        t = t.replaceAll("(?m)^Category:.*$", "");

        // Remove leftover pipes often seen in image/template markup
        t = t.replace("|", " ");

        // Collapse whitespace
        t = t.replaceAll("\\s{2,}", " ").trim();

        return t;
    }
}

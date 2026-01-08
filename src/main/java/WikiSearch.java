import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;

public class WikiSearch {

    private static final String INDEX_PATH =
            "/Volumes/T7 Shield/wiki_pipeline/index/simplewiki_lucene";

    public static String searchTopSnippet(String question) {

        try {
            FSDirectory dir = FSDirectory.open(Path.of(INDEX_PATH));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();

            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(QueryParser.escape(question));

            TopDocs hits = searcher.search(query, 1);

            if (hits.scoreDocs.length == 0) {
                reader.close();
                return "No relevant information found.";
            }

            ScoreDoc topHit = hits.scoreDocs[0];
            Document doc = searcher.storedFields().document(topHit.doc);

            String title = doc.get("title");
            String content = doc.get("content");

            reader.close();

            if (content == null || content.isBlank()) {
                return "No readable content found.";
            }

            if (content.length() > 500) {
                content = content.substring(0, 500) + "...";
            }

            return title + "\n\n" + content;

        } catch (Exception e) {
            return "Search error: " + e.getMessage();
        }
    }
}

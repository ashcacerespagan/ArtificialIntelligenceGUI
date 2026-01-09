import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WikiIndex {

    private static final Path JSONL_PATH = Path.of("/Volumes/T7 Shield/wiki_pipeline/text/simplewiki.jsonl");
    private static final Path INDEX_PATH = Path.of("/Volumes/T7 Shield/wiki_pipeline/index/simplewiki_lucene");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(INDEX_PATH);

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // rebuild from scratch each run

        try (FSDirectory dir = FSDirectory.open(INDEX_PATH);
             IndexWriter writer = new IndexWriter(dir, config);
             BufferedReader br = Files.newBufferedReader(JSONL_PATH, StandardCharsets.UTF_8)) {

            String line;
            long count = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                JSONObject obj;
                try {
                    obj = new JSONObject(line);
                } catch (Exception e) {
                    continue; // skip malformed lines
                }

                String title = obj.optString("title", "").trim();
                String text = obj.optString("text", "").trim();

                if (title.isEmpty() || text.isEmpty()) continue;

                Document doc = new Document();
                doc.add(new StringField("title", title, Field.Store.YES));
                doc.add(new TextField("content", text, Field.Store.YES));

                writer.addDocument(doc);

                count++;
                if (count % 10_000 == 0) {
                    System.out.println("Indexed " + count + " articles...");
                }
            }

            writer.commit();
            System.out.println("Done. Total indexed: " + count);
            System.out.println("Index saved to: " + INDEX_PATH);
        }
    }
}

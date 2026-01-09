/**
 * @param source  OFFLINE / ONLINE (string for now)
 * @param docPath offline file path (nullable)
 * @param url     online URL (nullable)
 */
public record SearchResult(String title, String snippet, String source, String docPath, String url) {
}

import java.util.List;

/**
 * @param shortAnswer null for now
 */
public record SearchResponse(String shortAnswer, List<SearchResult> results) {
}

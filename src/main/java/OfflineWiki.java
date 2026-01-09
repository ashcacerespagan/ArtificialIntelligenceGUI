import java.util.List;

public class OfflineWiki implements Knowledge {
    @Override
    public SearchResponse search(String query) {
        List<SearchResult> results = WikiSearch.search(query, 5);
        return new SearchResponse(null, results);
    }
}

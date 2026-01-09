import java.util.List;

public class OnlineWiki implements Knowledge {
    @Override
    public SearchResponse search(String query) {
        return new SearchResponse(
                null,
                List.of(new SearchResult(
                        "Online mode not implemented",
                        "Online mode is currently disabled. Uncheck AI Mode to use offline Wikipedia search.",
                        "ONLINE",
                        null,
                        null
                ))
        );
    }
}

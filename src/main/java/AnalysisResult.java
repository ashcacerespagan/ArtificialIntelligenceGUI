/**
 * @param queryType         FACTOID, DEFINITION, LIST, CONVERSATION, GENERAL
 * @param offlineConfidence High / Medium / Low / N/A
 */
public record AnalysisResult(String queryType, boolean isQuestion, String offlineConfidence, String detectedEntity) {
}
import java.util.regex.Pattern;

public class QueryAnalyzer {

    private static final Pattern CONVERSATION =
            Pattern.compile("^(hi|hello|hey|how are you|what's up|thanks|thank you)\\b.*",
                    Pattern.CASE_INSENSITIVE);

    public static AnalysisResult analyze(String query) {
        String q = query.trim();
        String lower = q.toLowerCase();
        boolean isQuestion = lower.endsWith("?") || lower.startsWith("who ")
                || lower.startsWith("what ") || lower.startsWith("when ")
                || lower.startsWith("where ") || lower.startsWith("how ");

        // Intent type
        String type = "GENERAL";

        if (CONVERSATION.matcher(lower).matches()) {
            type = "CONVERSATION";
        } else if (lower.startsWith("how old") || lower.startsWith("when did") || lower.startsWith("where is")
                || lower.startsWith("who is") || lower.startsWith("who was")) {
            type = "FACTOID";
        } else if (lower.startsWith("what is") || lower.startsWith("define") || lower.startsWith("explain")) {
            type = "DEFINITION";
        } else if (lower.startsWith("list") || lower.contains("top ") || lower.contains("best ")) {
            type = "LIST";
        }

        // Offline confidence (simple, but useful)
        String confidence = switch (type) {
            case "FACTOID", "DEFINITION" -> "High";
            case "LIST" -> "Low";
            case "CONVERSATION" -> "N/A";
            default -> "Medium";
        };

        // Entity guess: first capitalized “chunk” (very lightweight)
        String entity = guessEntity(q);

        return new AnalysisResult(type, isQuestion, confidence, entity);
    }

    private static String guessEntity(String q) {
        // Find first capitalized word that isn’t the first word in the sentence like "What/How/When"
        String[] parts = q.split("\\s+");
        for (String p : parts) {
            if (p.length() > 1 && Character.isUpperCase(p.charAt(0))) {
                String cleaned = p.replaceAll("[^A-Za-z0-9\\-]", "");
                if (!cleaned.isBlank()) return cleaned;
            }
        }
        return "Unknown";
    }
}

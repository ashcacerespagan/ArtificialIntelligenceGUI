import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard extends Application {

    private TextArea txtUserInput;
    private TextArea txtAIResponse;
    private CheckBox chkAIEnabled;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AI Dashboard");

        Label lblUserInput = new Label("Enter Your Query");
        txtUserInput = new TextArea();
        txtUserInput.setPrefHeight(60);

        Label lblAIResponse = new Label("AI Response");
        txtAIResponse = new TextArea();
        txtAIResponse.setEditable(false);
        txtAIResponse.setWrapText(true);

        Button btnPredict = new Button("Predict");
        Button btnAnalyze = new Button("Analyze");
        Button btnReset = new Button("Reset");
        chkAIEnabled = new CheckBox("Enable AI Mode");

        btnPredict.setOnAction(e -> handlePredict());
        btnAnalyze.setOnAction(e -> handleAnalyze());
        btnReset.setOnAction(e -> {
            txtUserInput.clear();
            txtAIResponse.clear();
        });

        HBox buttonBox = new HBox(10, btnPredict, btnAnalyze, btnReset);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                lblUserInput, txtUserInput,
                lblAIResponse, txtAIResponse,
                chkAIEnabled, buttonBox
        );

        Scene scene = new Scene(layout, 550, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handlePredict() {
        String input = txtUserInput.getText().trim();
        if (input.isEmpty()) {
            txtAIResponse.setText("Please enter a question.");
            return;
        }

        try {
            // Phase 2: analyze intent first (so we can avoid pointless searches)
            AnalysisResult analysis = QueryAnalyzer.analyze(input);

            // Simple “conversation” gate (still Phase 2: intent handling)
            if ("CONVERSATION".equals(analysis.queryType())) {
                txtAIResponse.setText(
                        "This app is focused on factual / Wikipedia-style questions.\n" +
                                "Try: \"What is X?\", \"Who is Y?\", or \"When did Z happen?\""
                );
                return;
            }

            // Phase 1: route to a Knowledge source
            Knowledge source = chkAIEnabled.isSelected()
                    ? new OnlineWiki()   // stub for now
                    : new OfflineWiki();

            SearchResponse response = source.search(input);

            StringBuilder out = new StringBuilder();

            // Include analysis summary (optional, but helpful for v2)
            out.append("Intent: ").append(analysis.queryType())
                    .append(" | Offline Confidence: ").append(analysis.offlineConfidence())
                    .append("\n\n");

            // Short answer not implemented yet (Phase 3+), but keep the slot
            if (response.shortAnswer() != null && !response.shortAnswer().isBlank()) {
                out.append("Short Answer:\n")
                        .append(response.shortAnswer().trim())
                        .append("\n\n");
            }

            if (response.results() == null || response.results().isEmpty()) {
                out.append("No results found.");
            } else {
                SearchResult top = response.results().getFirst();

                out.append("Top Result (")
                        .append(top.source())
                        .append("):\n")
                        .append(top.title())
                        .append("\n\n")
                        .append(top.snippet());
            }

            txtAIResponse.setText(out.toString());

        } catch (Exception ex) {
            txtAIResponse.setText("Predict error: " + ex.getMessage());
        }
    }


    private void handleAnalyze() {
        String input = txtUserInput.getText().trim();
        if (input.isEmpty()) {
            txtAIResponse.setText("Please enter something to analyze.");
            return;
        }

        AnalysisResult ar = QueryAnalyzer.analyze(input);

        String[] words = input.split("\\s+");
        int wordCount = (input.isBlank()) ? 0 : words.length;
        int charCount = input.replaceAll("\\s+", "").length();

        StringBuilder analysis = new StringBuilder();
        analysis.append("Analysis Results:\n");
        analysis.append("- Word count: ").append(wordCount).append("\n");
        analysis.append("- Character count (no spaces): ").append(charCount).append("\n");
        analysis.append("- Query type: ").append(ar.queryType()).append("\n");
        analysis.append("- Offline confidence: ").append(ar.offlineConfidence()).append("\n");
        analysis.append("- Is question: ").append(ar.isQuestion()).append("\n");

        if (!ar.detectedEntity().equals("Unknown")) {
            analysis.append("- Detected entity: ").append(ar.detectedEntity()).append("\n");
        }

        txtAIResponse.setText(analysis.toString());
    }
}

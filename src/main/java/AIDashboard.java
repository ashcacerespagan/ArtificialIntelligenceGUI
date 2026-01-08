import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AIDashboard extends Application {

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

        // OFFLINE MODE — Lucene Wikipedia
        if (!chkAIEnabled.isSelected()) {
            String answer = WikiSearch.searchTopSnippet(input);
            txtAIResponse.setText("AI Response: " + answer);
            return;
        }

        // ONLINE MODE (future API)
        txtAIResponse.setText("Online mode is currently disabled. Uncheck AI Mode to use offline Wikipedia search.");
    }

    private void handleAnalyze() {
        String input = txtUserInput.getText().trim();
        if (input.isEmpty()) {
            txtAIResponse.setText("Please enter something to analyze.");
            return;
        }

        // You can decide later if "Analyze" should also have an online version.
        // For now, keep it offline and consistent.

        StringBuilder analysis = new StringBuilder();
        analysis.append("Analysis Results:\n");

        // Word and character count
        String[] words = input.split("\\s+");
        int wordCount = words.length;
        int charCount = input.replaceAll("\\s+", "").length();
        analysis.append("- Word count: ").append(wordCount).append("\n");
        analysis.append("- Character count (no spaces): ").append(charCount).append("\n");

        // Very basic sentiment (toy logic, but OK for a demo)
        String lower = input.toLowerCase();
        if (lower.contains("happy") || lower.contains("great") || lower.contains("love")) {
            analysis.append("- Sentiment: Positive\n");
        } else if (lower.contains("hate") || lower.contains("angry") || lower.contains("sad")) {
            analysis.append("- Sentiment: Negative\n");
        } else {
            analysis.append("- Sentiment: Neutral\n");
        }

        // Other detections
        if (input.matches("[-+*/().0-9 ]+")) {
            analysis.append("- Detected possible math expression.\n");
        }
        if (input.endsWith("?")) {
            analysis.append("- Detected question format.\n");
        }

        txtAIResponse.setText(analysis.toString());
    }
}

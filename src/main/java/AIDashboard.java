import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        btnAnalyze.setOnAction(e -> txtAIResponse.setText("Analysis functionality not implemented yet."));
        btnReset.setOnAction(e -> {
            txtUserInput.clear();
            txtAIResponse.clear();
        });

        HBox buttonBox = new HBox(10, btnPredict, btnAnalyze, btnReset);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(lblUserInput, txtUserInput, lblAIResponse, txtAIResponse, chkAIEnabled, buttonBox);

        Scene scene = new Scene(layout, 550, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handlePredict() {
        String input = txtUserInput.getText().trim();

        if (input.isEmpty()) {
            txtAIResponse.setText("Please enter a query.");
            return;
        }

        if (chkAIEnabled.isSelected()) {
            txtAIResponse.setText("Contacting AI... Please wait.");
            new Thread(() -> {
                String aiResponse = callOpenAI(input);
                Platform.runLater(() -> txtAIResponse.setText("AI Response: " + aiResponse));
            }).start();
        } else {
            simulateResponse(input);
        }
    }

    private void simulateResponse(String input) {
        String question = input.toLowerCase();
        String response;

        try {
            if (question.matches("[-+*/().0-9 ]+")) {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                Object result = engine.eval(input);
                txtAIResponse.setText("AI Response: " + input + " = " + result);
                return;
            }
        } catch (Exception ignored) {}

        if (question.contains("hello") || question.contains("hi")) {
            response = "Hello! How can I assist you today?";
        } else if (question.contains("how are you")) {
            response = "I'm functioning as expected. How about you?";
        } else if (question.contains("your name")) {
            response = "I'm the AI Dashboard — your simulated assistant.";
        } else if (question.contains("who is the president")) {
            response = "As of 2025, the U.S. president is Joe Biden.";
        } else if (question.contains("capital of france")) {
            response = "The capital of France is Paris.";
        } else if (question.contains("capital of japan")) {
            response = "Tokyo is the capital of Japan.";
        } else if (question.contains("fastest animal")) {
            response = "The cheetah is the fastest land animal.";
        } else if (question.contains("largest planet")) {
            response = "Jupiter is the largest planet in our solar system.";
        } else if (question.contains("who invented the internet")) {
            response = "The invention of the internet involved many people, but Vint Cerf and Bob Kahn are often credited.";
        } else if (question.contains("how do airplanes fly")) {
            response = "Airplanes fly by generating lift with their wings as they move through the air.";
        } else if (question.contains("why is the sky blue")) {
            response = "Because of Rayleigh scattering — shorter blue light wavelengths scatter more in the atmosphere.";
        } else if (question.contains("how do plants grow")) {
            response = "Plants grow using sunlight, water, and nutrients in a process called photosynthesis.";
        } else if (question.contains("what is the meaning of life")) {
            response = "That’s a deep one! Many say it's 42 — but you decide what it means to you.";
        } else if (question.contains("joke")) {
            response = "Why did the developer go broke? Because they used up all their cache.";
        } else if (question.contains("favorite color")) {
            response = "I'd say grayscale, but I hear blue is quite popular.";
        } else if (question.contains("time")) {
            response = "I don't have access to your clock, but I'm always here!";
        } else if (question.contains("weather")) {
            response = "I can't fetch real weather, but it's sunny in this interface.";
        } else if (question.contains("translate hello")) {
            response = "\"Hello\" in Spanish is \"Hola\".";
        } else if (question.contains("can you help")) {
            response = "Absolutely! Ask me anything.";
        } else if (question.contains("thank you")) {
            response = "You're welcome!";
        } else if (question.contains("goodbye") || question.contains("bye")) {
            response = "Goodbye! Come back anytime.";
        } else {
            response = simulateFallbackResponse(question);
        }

        txtAIResponse.setText("AI Response: " + response);
    }

    private String simulateFallbackResponse(String question) {
        if (question.contains("who")) return "That's an interesting person — I'd need more context.";
        if (question.contains("what")) return "I'm not sure, but it sounds important!";
        if (question.contains("when")) return "Timing can be tricky. Can you be more specific?";
        if (question.contains("why")) return "Great question! Sometimes we ask to understand better.";
        if (question.contains("how")) return "It depends! Try asking in more detail.";
        return "I'm still learning — ask me another way!";
    }

    private String callOpenAI(String prompt) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        String apiURL = "https://api.openai.com/v1/chat/completions";

        String requestBody = """
        {
          "model": "gpt-3.5-turbo",
          "messages": [{"role": "user", "content": "%s"}],
          "max_tokens": 150
        }
        """.formatted(prompt.replace("\"", "\\\""));

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();


            JSONObject json = new JSONObject(responseBody);

            if (json.has("error")) {
                String message = json.getJSONObject("error").optString("message", "An unknown error occurred.");
                return "AI Mode Unavailable: " + message;
            }

            JSONArray choices = json.getJSONArray("choices");
            JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");
            return messageObj.getString("content").trim();


        } catch (Exception e) {
            return "Failed to contact AI: " + e.getMessage();
        }
    }
}

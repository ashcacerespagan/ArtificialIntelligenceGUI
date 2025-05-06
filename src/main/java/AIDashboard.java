import javax.swing.*;
import java.awt.*;

public class AIDashboard extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea outputArea;

    public AIDashboard() {
        // Set up the main frame
        setTitle("AI Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panels
        JPanel inputPanel = new JPanel();
        JPanel outputPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        // Set up input area
        inputArea = new JTextArea(5, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Your Query"));
        inputPanel.add(inputScroll);

        // Set up output area
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputPanel.setBorder(BorderFactory.createTitledBorder("AI Response"));
        outputPanel.add(outputScroll);

        // Set up buttons
        JButton predictButton = new JButton("Predict");
        JButton analyzeButton = new JButton("Analyze");
        JButton resetButton = new JButton("Reset");

        // Add action listeners to buttons
        predictButton.addActionListener(e -> handlePrediction());

        analyzeButton.addActionListener(e -> handleAnalysis());

        resetButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        buttonPanel.add(predictButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(resetButton);

        // Add panels to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handlePrediction() {
        String inputText = inputArea.getText();
        if (inputText.isEmpty()) {
            outputArea.setText("Please enter a query for prediction.");
        } else {
            // Simulate a prediction response
            outputArea.setText("Predicted response based on input: \"" + inputText + "\"\n(Simulated AI Prediction)");
        }
    }

    private void handleAnalysis() {
        String inputText = inputArea.getText();
        if (inputText.isEmpty()) {
            outputArea.setText("Please enter data to analyze.");
        } else {
            // Simulate an analysis response
            outputArea.setText("Analyzed response based on input: \"" + inputText + "\"\n(Simulated AI Analysis)");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AIDashboard dashboard = new AIDashboard();
            dashboard.setVisible(true);
        });
    }
}

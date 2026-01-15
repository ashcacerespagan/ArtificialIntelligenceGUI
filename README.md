# ArtificialIntelligenceGUI

I created this project as a creative interface mockup that explores what an AI assistant GUI might look like. While the functionality is rule-based rather than truly intelligent, it let me focus on delivering a futuristic and clean user interface, combining theme consistency with conditional logic. This helped me practice scene switching, input handling, and custom styling within JavaFX.

---

### ✨ Features

- 🔮 **Predict Button**  
  Simulates AI chatbot behavior with hardcoded responses to common user prompts. Also handles basic math expression evaluation using JavaScript engine fallback.

- 🧠 **Analyze Button**  
  Performs simple offline natural language analysis when AI Mode is disabled:
  - Word and character count
  - Sentiment analysis (positive/neutral/negative)
  - Question and math expression detection  
  If AI Mode is enabled but no API key is available, it shows a user-friendly fallback message instead of crashing.

- 🤖 **AI Mode (Optional)**  
  If enabled and configured with an OpenAI API key via `.env`, queries are sent to OpenAI's GPT-3.5-Turbo model. If the key is missing or the quota is exceeded, a clear message is displayed in the response area.

---

### 🛠️ Tech Stack

- Java 21
- JavaFX 21
- Maven
- exp4j (optional math parsing)
- Dotenv (optional `.env` integration for API key)

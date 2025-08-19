# Artificial-Intelligence-Chatbot
🤖 Artificial Intelligence Chatbot (Java + NLP)

📌 Project Overview

This project is a Java-based Artificial Intelligence Chatbot developed as part of internship tasks.
The chatbot is capable of interactive communication using Natural Language Processing (NLP) techniques and a rule-based + TF-IDF similarity approach for responding to user queries.
It also features a Swing GUI for real-time interaction and a simple training mechanism to teach the bot new answers.


🚀 Features

✅ Interactive GUI built with Java Swing.

✅ Natural Language Processing (NLP) for text preprocessing (tokenization, case normalization, stop-word removal).

✅ Machine Learning Logic using TF-IDF + Cosine Similarity to match user queries with FAQ dataset.

✅ Rule-based answers for common greetings, thanks, and exit queries.

✅ Dynamic Training – users can add new Question/Answer pairs through the GUI.

✅ Real-time Communication – type in your message and get instant responses.


🏗 Project Structure

📂 AIChatbot
 ┣ 📜 AIChatbot.java      # Main chatbot program with GUI + NLP logic
 ┣ 📜 README.md           # Project documentation
 ┗ 📂 resources           # (Optional) Dataset / future enhancements

🔧 Installation & Usage

1. Clone / Download the project

git clone https://github.com/yourusername/AIChatbot.git

2. Compile the project

javac AIChatbot.java

3. Run the chatbot

java AIChatbot

💬 Example Interaction

User Input: Hello
Bot Response: Hello! How can I help you today?

User Input: What is NLP?
Bot Response: Natural Language Processing (NLP) helps computers understand, interpret, and generate human language.

User Input: What is the time?
Bot Response: It's 2025-08-19 14:30 (system time).

User Input: Tell me about space travel
Bot Response: I’m not fully sure about that yet. Could you rephrase?   Tip: You can also click “Train” to teach me the right answer!


📊 Technical Concepts Used

Java Swing for GUI design.

NLP Preprocessing: Lowercasing, tokenization, stop-word removal.

TF-IDF Vectorization to represent text numerically.

Cosine Similarity to match user queries with FAQs.

HashMap-based Knowledge Base for storing Q/A pairs.


🌟 Future Enhancements

🔹 Connect to a real database for storing FAQs.

🔹 Add speech-to-text and text-to-speech capabilities.

🔹 Deploy as a web-based chatbot using Java Spring Boot.

🔹 Integrate with machine learning models (e.g., Naive Bayes, Transformers).


👩‍💻 Author

SHAIK SABIYA
Internship Project – Java Programming - CodeAlpha

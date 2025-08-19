# Artificial-Intelligence-Chatbot
**🤖 Artificial Intelligence Chatbot** (Java + NLP)

**📌 Project Overview**

This project is a Java-based Artificial Intelligence Chatbot developed as part of internship tasks.
The chatbot is capable of interactive communication using Natural Language Processing (NLP) techniques and a rule-based + TF-IDF similarity approach for responding to user queries.
It also features a Swing GUI for real-time interaction and a simple training mechanism to teach the bot new answers.


**🚀 Features**

✅ Interactive GUI built with Java Swing.

✅ Natural Language Processing (NLP) for text preprocessing (tokenization, case normalization, stop-word removal).

✅ Machine Learning Logic using TF-IDF + Cosine Similarity to match user queries with FAQ dataset.

✅ Rule-based answers for common greetings, thanks, and exit queries.

✅ Dynamic Training – users can add new Question/Answer pairs through the GUI.

✅ Real-time Communication – type in your message and get instant responses.


**🏗 Project Structure**

**📂 AIChatbot**
 ┣ 📜 AIChatbot.java      # Main chatbot program with GUI + NLP logic
 ┣ 📜 README.md           # Project documentation
 ┗ 📂 resources           # (Optional) Dataset / future enhancements

**🔧 Installation & Usage**

**1. Clone / Download the project**

git clone https://github.com/yourusername/AIChatbot.git

**2. Compile the project**

javac AIChatbot.java

**3. Run the chatbot**

java AIChatbot

**Example Interaction**

User Input: Hello
Bot Response: Hello! How can I help you today?

User Input: What is NLP?
Bot Response: Natural Language Processing (NLP) helps computers understand, interpret, and generate human language.

User Input: What is the time?
Bot Response: It's 2025-08-19 14:30 (system time).

User Input: Tell me about space travel
Bot Response: I’m not fully sure about that yet. Could you rephrase?   Tip: You can also click “Train” to teach me the right answer!


**📊 Technical Concepts Used**

Java Swing for GUI design.

NLP Preprocessing: Lowercasing, tokenization, stop-word removal.

TF-IDF Vectorization to represent text numerically.

Cosine Similarity to match user queries with FAQs.

HashMap-based Knowledge Base for storing Q/A pairs.


**🌟 Future Enhancements**

🔹 Connect to a real database for storing FAQs.

🔹 Add speech-to-text and text-to-speech capabilities.

🔹 Deploy as a web-based chatbot using Java Spring Boot.

🔹 Integrate with machine learning models (e.g., Naive Bayes, Transformers).

I built you a complete Java chatbot app (hybrid NLP + Naive Bayes + rule-based) with a Swing GUI—ready to compile and run.

**How to run**

1. Save the canvas code as ChatbotApp.java


2. Compile: javac ChatbotApp.java


3. Run: java ChatbotApp



**What you get**

NLP preprocessing (tokenization, stopwords, light stemming)

Naive Bayes intent classifier trained on FAQ examples (shipping, returns, hours, contact, courses, internships)

Rule-based smarts (greetings, thanks, date/time, simple math like 12+7, order tracking, mood nudges)

Swing GUI with Enter/Send, and Ctrl+L to clear chat

Notes & tips:

The TF-IDF + cosine approach chooses the closest FAQ answer; you can improve accuracy by expanding the stopword list and adding more FAQs.

You can add more domain-specific preprocessing (stemming, synonyms) to improve matching.

The knowledge.json file is created in the current working directory — training is persisted there.

If you want a web interface instead of Swing, tell me and I’ll produce a simple Spring Boot + Thymeleaf or a minimal Spark Java web app version.


**👩‍💻 Author**

**SHAIK SABIYA****
**Internship Project –> Java Programming -> CodeAlpha**
**Thankyou..**

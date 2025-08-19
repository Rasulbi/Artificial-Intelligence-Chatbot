import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Rectified AI Chatbot
 *
 * - Fixed compile and encoding errors
 * - Replaced PorterStemmer with a safe SimpleStemmer
 * - Removed non-ASCII characters from string literals
 *
 * How to run:
 *   javac AIChatbot.java
 *   java AIChatbot
 */
public class AIChatbot {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI(new Chatbot()).show());
    }

    /* ----------------------------- GUI LAYER ----------------------------- */

    static class ChatGUI {
        private final Chatbot bot;
        private final JFrame frame = new JFrame("AI Chatbot - Task 3");
        private final JTextArea transcript = new JTextArea();
        private final JTextField input = new JTextField();
        private final JButton sendBtn = new JButton("Send");
        private final JButton trainBtn = new JButton("Train");
        private final JLabel status = new JLabel("Ready");

        ChatGUI(Chatbot bot) {
            this.bot = bot;
            setupUI();
            wireEvents();
            greet();
        }

        void show() {
            frame.setVisible(true);
        }

        private void setupUI() {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(840, 600);
            frame.setLocationRelativeTo(null);

            transcript.setEditable(false);
            transcript.setLineWrap(true);
            transcript.setWrapStyleWord(true);
            transcript.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

            var inputPanel = new JPanel(new BorderLayout(8, 8));
            inputPanel.add(input, BorderLayout.CENTER);
            var rightBtns = new JPanel(new GridLayout(1, 2, 8, 8));
            rightBtns.add(sendBtn);
            rightBtns.add(trainBtn);
            inputPanel.add(rightBtns, BorderLayout.EAST);

            var root = new JPanel(new BorderLayout(8, 8));
            root.setBorder(new EmptyBorder(10, 10, 10, 10));
            root.add(new JScrollPane(transcript), BorderLayout.CENTER);
            root.add(inputPanel, BorderLayout.SOUTH);
            root.add(status, BorderLayout.NORTH);

            frame.setContentPane(root);

            // mnemonic for accessibility (Enter is not a typical mnemonic key but OK)
            sendBtn.setMnemonic(KeyEvent.VK_S);
        }

        private void wireEvents() {
            sendBtn.addActionListener(e -> send());
            input.addActionListener(e -> send());
            trainBtn.addActionListener(e -> openTrainDialog());

            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowOpened(WindowEvent e) {
                    input.requestFocusInWindow();
                }
            });
        }

        private void greet() {
            appendBot("Hi! I am your AI Chatbot.\n" +
                    "- Ask me anything.\n" +
                    "- I am trained on FAQs and use NLP + TF-IDF.\n" +
                    "- Click \"Train\" to teach me new Q&A.\n");
        }

        private void send() {
            String text = input.getText().trim();
            if (text.isEmpty()) return;
            appendUser(text);
            input.setText("");
            status.setText("Thinking...");
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override protected String doInBackground() {
                    return bot.respond(text);
                }
                @Override protected void done() {
                    try {
                        appendBot(get());
                    } catch (Exception ex) {
                        appendBot("Oops, something went wrong: " + ex.getMessage());
                    } finally {
                        status.setText("Ready");
                        input.requestFocusInWindow();
                    }
                }
            };
            worker.execute();
        }

        private void openTrainDialog() {
            JTextField qField = new JTextField();
            JTextArea aArea = new JTextArea(6, 30);
            aArea.setLineWrap(true);
            aArea.setWrapStyleWord(true);

            JPanel panel = new JPanel(new BorderLayout(8, 8));
            panel.add(new JLabel("Question (user might ask):"), BorderLayout.NORTH);
            panel.add(qField, BorderLayout.CENTER);
            JPanel ansPanel = new JPanel(new BorderLayout(8, 8));
            ansPanel.add(new JLabel("Answer (bot should reply):"), BorderLayout.NORTH);
            ansPanel.add(new JScrollPane(aArea), BorderLayout.CENTER);

            JPanel wrapper = new JPanel(new BorderLayout(12, 12));
            wrapper.add(panel, BorderLayout.NORTH);
            wrapper.add(ansPanel, BorderLayout.CENTER);
            wrapper.setPreferredSize(new Dimension(520, 300));

            int result = JOptionPane.showConfirmDialog(frame, wrapper, "Train FAQ",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String q = qField.getText().trim();
                String a = aArea.getText().trim();
                if (q.isEmpty() || a.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Both Question and Answer are required.",
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                bot.addFaq(q, a);
                appendSystem("Training added OK\nQ: " + q + "\nA: " + a);
            }
        }

        private void appendUser(String text) {
            transcript.append("YOU: " + text + "\n");
            scrollToEnd();
        }
        private void appendBot(String text) {
            transcript.append("BOT: " + text + "\n\n");
            scrollToEnd();
        }
        private void appendSystem(String text) {
            transcript.append("[SYSTEM] " + text + "\n\n");
            scrollToEnd();
        }
        private void scrollToEnd() {
            transcript.setCaretPosition(transcript.getDocument().getLength());
        }
    }

    /* --------------------------- CHATBOT LOGIC --------------------------- */

    static class Chatbot {
        private final NLP nlp = new NLP();
        private final TFIDF tfidf = new TFIDF(nlp);
        private final List<FAQ> knowledgeBase = new ArrayList<>();
        private final List<String> faqQuestions = new ArrayList<>();
        private final Map<Pattern, String> ruleResponses = buildRules();
        private double[][] faqVectors = new double[0][0]; // TF-IDF vectors cache

        Chatbot() {
            seedFAQs();
            rebuildVectors();
        }

        public String respond(String userInput) {
            if (userInput == null || userInput.isBlank()) {
                return "Please type something.";
            }

            // 1) Rule-based quick wins
            String ruleHit = tryRuleBased(userInput);
            if (ruleHit != null) return ruleHit;

            // 2) FAQ retrieval with TF-IDF cosine similarity
            String best = bestFAQAnswer(userInput);
            if (best != null) return best;

            // 3) Fallback small talk / guidance
            return "I am not sure about that yet. Could you rephrase?\n" +
                   "Tip: You can also click \"Train\" to teach me the right answer!";
        }

        public void addFaq(String question, String answer) {
            knowledgeBase.add(new FAQ(question, answer));
            faqQuestions.add(question);
            rebuildVectors();
        }

        /* ------------------------- Private Helpers ------------------------ */

        private String tryRuleBased(String input) {
            String text = input.toLowerCase(Locale.ROOT).trim();

            // date/time
            if (text.contains("time") || text.matches(".\\bwhat('s| is) the time\\b.")) {
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                return "It's " + now + " (system time).";
            }
            if (text.contains("date") || text.matches(".\\bwhat('s| is) the date\\b.")) {
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return "Today's date is " + now + ".";
            }

            for (var entry : ruleResponses.entrySet()) {
                if (entry.getKey().matcher(text).find()) {
                    return entry.getValue();
                }
            }
            return null;
        }

        private String bestFAQAnswer(String userInput) {
            if (knowledgeBase.isEmpty()) return null;

            double[] qVec = tfidf.transformOne(userInput);
            int bestIdx = -1;
            double bestSim = -1.0;

            for (int i = 0; i < faqVectors.length; i++) {
                double sim = cosine(qVec, faqVectors[i]);
                if (sim > bestSim) {
                    bestSim = sim;
                    bestIdx = i;
                }
            }

            // Threshold controls how confident we need to be
            if (bestIdx >= 0 && bestSim >= 0.22) {
                return knowledgeBase.get(bestIdx).answer +
                        "\n\n(Found via FAQ match • similarity=" + String.format(Locale.US,"%.2f", bestSim) + ")";
            }
            return null;
        }

        private void rebuildVectors() {
            // Build list of questions from knowledgeBase at rebuild time to keep order
            faqQuestions.clear();
            for (FAQ f : knowledgeBase) faqQuestions.add(f.question);
            faqVectors = tfidf.fitTransform(faqQuestions);
        }

        private static double cosine(double[] a, double[] b) {
            if (a.length != b.length) return 0;
            double dot = 0, na = 0, nb = 0;
            for (int i = 0; i < a.length; i++) {
                dot += a[i] * b[i];
                na += a[i] * a[i];
                nb += b[i] * b[i];
            }
            if (na == 0 || nb == 0) return 0;
            return dot / (Math.sqrt(na) * Math.sqrt(nb));
        }

        private static Map<Pattern, String> buildRules() {
            Map<Pattern, String> m = new LinkedHashMap<>();
            m.put(Pattern.compile("\\b(hi|hello|hey|yo)\\b"), "Hello! How can I help you today?");
            m.put(Pattern.compile("\\b(thank(s| you)?)\\b"), "You're welcome!");
            m.put(Pattern.compile("\\b(bye|goodbye|see you|ttyl)\\b"), "Goodbye! Have a great day!");
            m.put(Pattern.compile("\\b(who are you|what are you)\\b"), "I am a Java-based AI chatbot using NLP + TF-IDF.");
            m.put(Pattern.compile("\\b(help|what can you do)\\b"),
                    "I can answer FAQs, handle small talk, and you can train me using the 'Train' button.");
            return m;
        }

        private void seedFAQs() {
            // Starter knowledge base (customize freely)
            addFaq("What is Artificial Intelligence?",
                    "Artificial Intelligence (AI) is the simulation of human intelligence in machines that are programmed to think and learn.");
            addFaq("What is Machine Learning?",
                    "Machine Learning (ML) is a subset of AI that enables systems to learn patterns from data and improve over time without being explicitly programmed.");
            addFaq("What is NLP?",
                    "Natural Language Processing (NLP) helps computers understand, interpret, and generate human language.");
            addFaq("How do I train this chatbot?",
                    "Click the 'Train' button, then add a sample question and the answer I should give.");
            addFaq("What technologies does this chatbot use?",
                    "It's built in Java with Swing for the GUI, NLP preprocessing, and TF-IDF similarity to retrieve FAQ answers.");
            addFaq("Can I integrate this with a web app?",
                    "Yes. Expose the Chatbot.respond() method via a REST controller (for example, Spring Boot) or WebSocket.");
            addFaq("How can I reset or clear training?",
                    "Restart the app to restore the default seeded FAQs. For persistence, save and load FAQs to a file (can be added easily).");
        }
    }

    /* ------------------------------- MODELS ------------------------------ */

    static class FAQ {
        final String question;
        final String answer;
        FAQ(String q, String a) { this.question = q; this.answer = a; }
        @Override public String toString() { return "FAQ{" + question + "}"; }
    }

    /* ------------------------------- NLP -------------------------------- */

    static class NLP {
        private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-z0-9 ]");
        private final Set<String> stopwords;

        private final SimpleStemmer stemmer = new SimpleStemmer();

        NLP() {
            stopwords = new HashSet<>(Arrays.asList(
                    "a","an","the","is","are","am","was","were","be","been","being",
                    "to","of","in","for","on","and","or","not","with",
                    "this","that","these","those","it","its",
                    "i","you","he","she","we","they","me","my","your","our","their",
                    "can","could","should","would","will","shall","may","might","do","does","did",
                    "what","when","where","why","how","which","who","whom","as","at","by","from",
                    "have","has","had","if","else","then","than","so","such","also","about"
            ));
        }

        List<String> tokenize(String text) {
            if (text == null) return Collections.emptyList();
            String lower = text.toLowerCase(Locale.ROOT);
            lower = NON_ALPHANUM.matcher(lower).replaceAll(" ");
            String[] parts = lower.trim().split("\\s+");
            List<String> tokens = new ArrayList<>();
            for (String p : parts) {
                if (p.isBlank()) continue;
                if (stopwords.contains(p)) continue;
                String stem = stemmer.stem(p);
                if (!stem.isBlank()) tokens.add(stem);
            }
            return tokens;
        }
    }

    /* --------------------------- TF-IDF VECTOR --------------------------- */

    static class TFIDF {
        private final NLP nlp;
        private List<String> vocab = new ArrayList<>();
        private Map<String, Integer> vocabIndex = new HashMap<>();
        private double[] idf = new double[0];

        TFIDF(NLP nlp) { this.nlp = nlp; }

        double[][] fitTransform(List<String> texts) {
            if (texts == null) texts = List.of();
            // Build vocabulary
            Map<String, Integer> df = new HashMap<>();
            List<List<String>> docs = new ArrayList<>();
            for (String t : texts) {
                List<String> tokens = nlp.tokenize(t);
                docs.add(tokens);
                Set<String> unique = new HashSet<>(tokens);
                for (String tok : unique) df.put(tok, df.getOrDefault(tok, 0) + 1);
            }

            vocab = df.keySet().stream().sorted().collect(Collectors.toList());
            vocabIndex.clear();
            for (int i = 0; i < vocab.size(); i++) vocabIndex.put(vocab.get(i), i);

            int N = Math.max(1, texts.size());
            idf = new double[vocab.size()];
            for (int i = 0; i < vocab.size(); i++) {
                int dfi = df.getOrDefault(vocab.get(i), 0);
                idf[i] = Math.log((N + 1.0) / (dfi + 1.0)) + 1.0; // smoothed IDF
            }

            double[][] out = new double[texts.size()][vocab.size()];
            for (int i = 0; i < docs.size(); i++) {
                out[i] = tfidfVector(docs.get(i));
            }
            return out;
        }

        double[] transformOne(String text) {
            List<String> tokens = nlp.tokenize(text);
            return tfidfVector(tokens);
        }

        private double[] tfidfVector(List<String> tokens) {
            double[] vec = new double[vocab.size()];
            if (vocab.isEmpty() || tokens.isEmpty()) return vec;

            Map<String, Integer> tf = new HashMap<>();
            for (String t : tokens) tf.put(t, tf.getOrDefault(t, 0) + 1);

            double sum = tokens.size();
            for (var e : tf.entrySet()) {
                Integer idx = vocabIndex.get(e.getKey());
                if (idx == null) continue;
                double tfNorm = e.getValue() / sum;
                vec[idx] = tfNorm * idf[idx];
            }
            return vec;
        }
    }

    /* --------------------------- SIMPLE STEMMER -------------------------- */
    /**
     * A lightweight and safe stemmer that removes common suffixes.
     * This is intentionally simple and robust (no invalid character literals).
     */
    static class SimpleStemmer {
        public String stem(String word) {
            if (word == null) return "";
            String w = word.toLowerCase(Locale.ROOT);
            if (w.length() <= 3) return w;
            // remove common endings
            if (w.endsWith("ing") && w.length() > 4) return w.substring(0, w.length() - 3);
            if (w.endsWith("ed") && w.length() > 3) return w.substring(0, w.length() - 2);
            if (w.endsWith("es") && w.length() > 3) return w.substring(0, w.length() - 2);
            if (w.endsWith("s") && w.length() > 3) return w.substring(0, w.length() - 1);
            return w;
        }
    }
}

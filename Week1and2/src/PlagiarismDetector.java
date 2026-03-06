import java.util.*;
import java.nio.file.*;
import java.io.*;

public class PlagiarismDetector {

    // n-gram length
    private int n;
    // n-gram -> set of document names
    private HashMap<String, Set<String>> nGramIndex;

    public PlagiarismDetector(int n) {
        this.n = n;
        nGramIndex = new HashMap<>();
    }

    // Read a document and return words
    private List<String> readWords(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath));
        content = content.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        String[] words = content.split("\\s+");
        return Arrays.asList(words);
    }

    // Extract n-grams from list of words
    private List<String> extractNGrams(List<String> words) {
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= words.size() - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words.get(i + j));
                if (j != n - 1) sb.append(" ");
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    // Index a document into n-gram hash map
    public void indexDocument(String docName, String filePath) throws IOException {
        List<String> words = readWords(filePath);
        List<String> ngrams = extractNGrams(words);

        for (String gram : ngrams) {
            nGramIndex.putIfAbsent(gram, new HashSet<>());
            nGramIndex.get(gram).add(docName);
        }

        System.out.println(docName + " → Extracted " + ngrams.size() + " n-grams");
    }

    // Analyze a document for similarity
    public void analyzeDocument(String docName, String filePath) throws IOException {
        List<String> words = readWords(filePath);
        List<String> ngrams = extractNGrams(words);

        // Map otherDoc -> matching n-gram count
        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {
            if (nGramIndex.containsKey(gram)) {
                for (String otherDoc : nGramIndex.get(gram)) {
                    if (!otherDoc.equals(docName)) {
                        matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        if (matchCount.isEmpty()) {
            System.out.println("No matches found for " + docName);
            return;
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            int matched = entry.getValue();
            double similarity = matched * 100.0 / ngrams.size();
            System.out.printf("→ Found %d matching n-grams with \"%s\"\n", matched, entry.getKey());
            System.out.printf("→ Similarity: %.2f%% %s\n",
                    similarity, similarity > 50 ? "(PLAGIARISM DETECTED)" : "(suspicious)");
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter n-gram length (e.g., 5): ");
        int n = sc.nextInt();
        sc.nextLine();

        PlagiarismDetector detector = new PlagiarismDetector(n);

        // Index existing documents
        System.out.print("Enter number of existing documents: ");
        int docCount = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < docCount; i++) {
            System.out.print("Enter document name: ");
            String docName = sc.nextLine();

            System.out.print("Enter file path: ");
            String path = sc.nextLine();

            detector.indexDocument(docName, path);
        }

        // Analyze new document
        System.out.print("\nEnter new document name to analyze: ");
        String newDoc = sc.nextLine();

        System.out.print("Enter file path: ");
        String newPath = sc.nextLine();

        detector.analyzeDocument(newDoc, newPath);

        sc.close();
    }
}
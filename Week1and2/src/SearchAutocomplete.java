import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
    boolean isEnd = false;
}

class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequencyMap = new HashMap<>();

    public void addQuery(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query, frequencyMap.get(query));
        }

        node.isEnd = true;
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();
            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : node.queries.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10)
                pq.poll();
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty())
            result.add(pq.poll().getKey());

        Collections.reverse(result);

        return result;
    }
}

public class SearchAutocomplete {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AutocompleteSystem system = new AutocompleteSystem();

        System.out.println("Commands:");
        System.out.println("1 query → add/update search");
        System.out.println("2 prefix → autocomplete");

        while (true) {

            int command = sc.nextInt();
            sc.nextLine();

            if (command == 1) {

                String query = sc.nextLine();
                system.addQuery(query);
                System.out.println("Frequency updated");

            } else if (command == 2) {

                String prefix = sc.nextLine();

                List<String> suggestions = system.search(prefix);

                System.out.println("Suggestions:");

                int rank = 1;
                for (String s : suggestions) {
                    System.out.println(rank + ". " + s);
                    rank++;
                }
            }
        }
    }
}
import java.util.*;

public class UsernameChecker {

    // username -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // username -> attempt frequency
    private HashMap<String, Integer> attempts = new HashMap<>();

    // Add user
    public void addUser(String username, int userId) {
        users.put(username, userId);
    }

    // Check availability
    public boolean checkAvailability(String username) {

        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        if (username.contains("_")) {
            String modified = username.replace("_", ".");
            if (!users.containsKey(modified)) {
                suggestions.add(modified);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String maxUser = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UsernameChecker system = new UsernameChecker();

        System.out.print("Enter number of existing users: ");
        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter userId: ");
            int id = sc.nextInt();
            sc.nextLine();

            system.addUser(username, id);
        }

        System.out.print("\nEnter username to check: ");
        String checkUser = sc.nextLine();

        boolean available = system.checkAvailability(checkUser);

        if (available) {
            System.out.println(checkUser + " → Available");
        } else {
            System.out.println(checkUser + " → Already Taken");
            System.out.println("Suggestions: " + system.suggestAlternatives(checkUser));
        }

        System.out.println("Most attempted username: " + system.getMostAttempted());

        sc.close();
    }
}
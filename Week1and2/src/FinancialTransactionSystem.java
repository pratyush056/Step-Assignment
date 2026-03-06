import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public void findTwoSum(int target) {

        Map<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction other = map.get(complement);

                System.out.println("Pair Found: (" + other.id + ", " + t.id + ")");
                return;
            }

            map.put(t.amount, t);
        }

        System.out.println("No pair found");
    }

    // Two-Sum with 1 hour window
    public void findTwoSumTimeWindow(int target) {

        Map<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                if (Math.abs(t.time - other.time) <= 3600000) {
                    System.out.println("Pair within 1 hour: (" + other.id + ", " + t.id + ")");
                    return;
                }
            }

            map.put(t.amount, t);
        }

        System.out.println("No pair within time window");
    }

    // Duplicate detection
    public void detectDuplicates() {

        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate Payments:");

                for (Transaction t : list) {
                    System.out.println("Transaction ID: " + t.id +
                            " Account: " + t.account +
                            " Amount: " + t.amount +
                            " Merchant: " + t.merchant);
                }
            }
        }
    }

    // K-Sum
    public void findKSum(int k, int target) {
        backtrack(new ArrayList<>(), 0, k, target);
    }

    private void backtrack(List<Transaction> current, int start, int k, int target) {

        if (k == 0 && target == 0) {

            System.out.print("K-Sum Found: ");

            for (Transaction t : current)
                System.out.print(t.id + " ");

            System.out.println();
            return;
        }

        if (k == 0 || target < 0)
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            current.add(t);

            backtrack(current, i + 1, k - 1, target - t.amount);

            current.remove(current.size() - 1);
        }
    }
}

public class FinancialTransactionSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        System.out.println("Commands:");
        System.out.println("1 id amount merchant account time → Add Transaction");
        System.out.println("2 target → Two-Sum");
        System.out.println("3 target → Two-Sum (1 hour window)");
        System.out.println("4 → Detect Duplicates");
        System.out.println("5 k target → K-Sum");

        while (true) {

            int command = sc.nextInt();

            if (command == 1) {

                int id = sc.nextInt();
                int amount = sc.nextInt();
                String merchant = sc.next();
                String account = sc.next();
                long time = sc.nextLong();

                analyzer.addTransaction(
                        new Transaction(id, amount, merchant, account, time));

            }

            else if (command == 2) {

                int target = sc.nextInt();
                analyzer.findTwoSum(target);

            }

            else if (command == 3) {

                int target = sc.nextInt();
                analyzer.findTwoSumTimeWindow(target);

            }

            else if (command == 4) {

                analyzer.detectDuplicates();

            }

            else if (command == 5) {

                int k = sc.nextInt();
                int target = sc.nextInt();

                analyzer.findKSum(k, target);
            }
        }
    }
}
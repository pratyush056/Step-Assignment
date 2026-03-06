import java.util.*;

public class FlashSaleInventory {

    // productId -> stockCount
    private HashMap<String, Integer> stockMap = new HashMap<>();

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();

    // Add product
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stock--;
            stockMap.put(productId, stock);
            return "Success, " + stock + " units remaining";
        }
        else {
            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;
            queue.put(userId, position);

            return "Added to waiting list, position #" + position;
        }
    }

    // Display waiting list
    public void showWaitingList(String productId) {
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("Waiting list is empty.");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() + " -> Position " + entry.getValue());
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        FlashSaleInventory system = new FlashSaleInventory();

        System.out.print("Enter number of products: ");
        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.print("Enter productId: ");
            String productId = sc.nextLine();

            System.out.print("Enter stock quantity: ");
            int stock = sc.nextInt();
            sc.nextLine();

            system.addProduct(productId, stock);
        }

        while (true) {

            System.out.println("\n1. Check Stock");
            System.out.println("2. Purchase Item");
            System.out.println("3. Show Waiting List");
            System.out.println("4. Exit");

            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter productId: ");
                String productId = sc.nextLine();

                int stock = system.checkStock(productId);
                System.out.println(productId + " → " + stock + " units available");
            }

            else if (choice == 2) {
                System.out.print("Enter productId: ");
                String productId = sc.nextLine();

                System.out.print("Enter userId: ");
                int userId = sc.nextInt();
                sc.nextLine();

                String result = system.purchaseItem(productId, userId);
                System.out.println(result);
            }

            else if (choice == 3) {
                System.out.print("Enter productId: ");
                String productId = sc.nextLine();
                system.showWaitingList(productId);
            }

            else {
                break;
            }
        }

        sc.close();
    }
}
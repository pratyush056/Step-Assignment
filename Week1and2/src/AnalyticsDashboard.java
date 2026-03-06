import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class RealTimeAnalytics {

    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> sourceCounts = new HashMap<>();

    public void processEvent(PageEvent event) {

        // Count page views
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Count traffic source
        sourceCounts.put(event.source, sourceCounts.getOrDefault(event.source, 0) + 1);
    }

    public void displayDashboard() {

        System.out.println("\n===== REAL TIME DASHBOARD =====");

        // Top pages using PriorityQueue
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        System.out.println("\nTop Pages:");
        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" + unique + " unique)");
            rank++;
        }

        // Traffic sources
        int total = sourceCounts.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("\nTraffic Sources:");
        for (String src : sourceCounts.keySet()) {
            int count = sourceCounts.get(src);
            double percent = (count * 100.0) / total;
            System.out.printf("%s: %.2f%%\n", src, percent);
        }

        System.out.println("===============================\n");
    }
}

public class AnalyticsDashboard {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        RealTimeAnalytics analytics = new RealTimeAnalytics();

        long lastUpdate = System.currentTimeMillis();

        System.out.println("Enter events: url userId source");
        System.out.println("Example: /article/news user123 google\n");

        while (true) {

            String url = sc.next();
            String userId = sc.next();
            String source = sc.next();

            PageEvent event = new PageEvent(url, userId, source);

            analytics.processEvent(event);

            // Update dashboard every 5 seconds
            long now = System.currentTimeMillis();
            if (now - lastUpdate >= 5000) {
                analytics.displayDashboard();
                lastUpdate = now;
            }
        }
    }
}
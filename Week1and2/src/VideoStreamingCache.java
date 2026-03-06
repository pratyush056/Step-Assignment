import java.util.*;

class Video {
    String videoId;
    String data;

    Video(String id, String data) {
        this.videoId = id;
        this.data = data;
    }
}

class MultiLevelCache {

    private final int L1_CAPACITY = 10000;
    private final int L2_CAPACITY = 100000;

    // L1 Cache (LRU)
    private LinkedHashMap<String, Video> L1 = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, Video> eldest) {
            return size() > L1_CAPACITY;
        }
    };

    // L2 Cache
    private HashMap<String, Video> L2 = new HashMap<>();

    // L3 Database
    private HashMap<String, Video> database = new HashMap<>();

    // Access count
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Statistics
    int L1Hits = 0;
    int L2Hits = 0;
    int L3Hits = 0;

    public Video getVideo(String videoId) {

        long start = System.nanoTime();

        // L1 Cache
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 Cache
        if (L2.containsKey(videoId)) {

            L2Hits++;
            System.out.println("L2 Cache HIT");

            Video v = L2.get(videoId);

            promoteToL1(v);

            return v;
        }

        System.out.println("L2 Cache MISS");

        // L3 Database
        if (database.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT");

            Video v = database.get(videoId);

            addToL2(v);

            return v;
        }

        System.out.println("Video not found");
        return null;
    }

    private void promoteToL1(Video v) {
        L1.put(v.videoId, v);
    }

    private void addToL2(Video v) {

        if (L2.size() >= L2_CAPACITY) {

            Iterator<String> it = L2.keySet().iterator();
            if (it.hasNext())
                L2.remove(it.next());
        }

        L2.put(v.videoId, v);
    }

    public void addVideo(String id, String data) {
        database.put(id, new Video(id, data));
    }

    public void invalidateVideo(String id) {

        L1.remove(id);
        L2.remove(id);
        database.remove(id);

        System.out.println("Video invalidated");
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        System.out.println("L1 Hits: " + L1Hits);
        System.out.println("L2 Hits: " + L2Hits);
        System.out.println("L3 Hits: " + L3Hits);

        if (total > 0) {
            System.out.println("L1 Hit Rate: " + (L1Hits * 100.0 / total) + "%");
            System.out.println("L2 Hit Rate: " + (L2Hits * 100.0 / total) + "%");
            System.out.println("L3 Hit Rate: " + (L3Hits * 100.0 / total) + "%");
        }
    }
}

public class VideoStreamingCache {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        MultiLevelCache cache = new MultiLevelCache();

        System.out.println("Commands:");
        System.out.println("1 videoId data → Add video to DB");
        System.out.println("2 videoId → Get video");
        System.out.println("3 videoId → Invalidate video");
        System.out.println("4 → Statistics");

        while (true) {

            int cmd = sc.nextInt();

            if (cmd == 1) {

                String id = sc.next();
                sc.nextLine();
                String data = sc.nextLine();

                cache.addVideo(id, data);

            }

            else if (cmd == 2) {

                String id = sc.next();
                cache.getVideo(id);

            }

            else if (cmd == 3) {

                String id = sc.next();
                cache.invalidateVideo(id);

            }

            else if (cmd == 4) {

                cache.getStatistics();
            }
        }
    }
}
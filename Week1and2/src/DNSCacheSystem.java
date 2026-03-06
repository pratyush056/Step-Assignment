import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCacheSystem {

    private int maxSize;
    private LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCacheSystem(int maxSize) {
        this.maxSize = maxSize;

        cache = new LinkedHashMap<String, DNSEntry>(maxSize, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCacheSystem.this.maxSize;
            }
        };
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217." + rand.nextInt(255) + "." + rand.nextInt(255);
    }

    // Resolve domain
    public synchronized String resolve(String domain, int ttl) {

        long startTime = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long endTime = System.nanoTime();
                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (retrieved in " + ((endTime - startTime) / 1_000_000.0) + " ms)");
                return entry.ipAddress;
            } else {
                System.out.println("Cache EXPIRED for " + domain);
                cache.remove(domain);
            }
        }

        misses++;

        String newIP = queryUpstreamDNS(domain);
        DNSEntry newEntry = new DNSEntry(domain, newIP, ttl);
        cache.put(domain, newEntry);

        long endTime = System.nanoTime();

        System.out.println("Cache MISS → Query upstream → " + newIP +
                " (lookup time " + ((endTime - startTime) / 1_000_000.0) + " ms)");

        return newIP;
    }

    // Cache statistics
    public void getCacheStats() {
        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Total Requests: " + total);
        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter cache size: ");
        int size = sc.nextInt();
        sc.nextLine();

        DNSCacheSystem dnsCache = new DNSCacheSystem(size);

        while (true) {

            System.out.println("\n1. Resolve Domain");
            System.out.println("2. Show Cache Stats");
            System.out.println("3. Exit");

            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {

                System.out.print("Enter domain: ");
                String domain = sc.nextLine();

                System.out.print("Enter TTL seconds: ");
                int ttl = sc.nextInt();
                sc.nextLine();

                dnsCache.resolve(domain, ttl);
            }

            else if (choice == 2) {
                dnsCache.getCacheStats();
            }

            else {
                break;
            }
        }

        sc.close();
    }
}
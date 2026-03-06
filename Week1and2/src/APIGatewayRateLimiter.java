import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    private int tokens;
    private final int maxTokens;
    private final double refillRate; // tokens per second
    private long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int) (seconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    public synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }

    public synchronized long retryAfterSeconds() {
        if (tokens > 0) return 0;

        return (long) (1 / refillRate);
    }
}

class RateLimiter {

    private final Map<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private final int LIMIT = 1000;
    private final double REFILL_RATE = 1000.0 / 3600.0; // tokens per second

    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId, new TokenBucket(LIMIT, REFILL_RATE));

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {
            int remaining = bucket.getRemainingTokens();
            return "Allowed (" + remaining + " requests remaining)";
        } else {
            long retry = bucket.retryAfterSeconds();
            return "Denied (0 requests remaining, retry after " + retry + "s)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        clientBuckets.putIfAbsent(clientId, new TokenBucket(LIMIT, REFILL_RATE));

        TokenBucket bucket = clientBuckets.get(clientId);

        int remaining = bucket.getRemainingTokens();
        int used = LIMIT - remaining;

        long resetTime = System.currentTimeMillis() / 1000 + 3600;

        return "{used: " + used + ", limit: " + LIMIT + ", reset: " + resetTime + "}";
    }
}

public class APIGatewayRateLimiter {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        RateLimiter limiter = new RateLimiter();

        System.out.println("Enter commands:");
        System.out.println("1 clientId  → checkRateLimit");
        System.out.println("2 clientId  → getRateLimitStatus");

        while (true) {

            int command = sc.nextInt();
            String clientId = sc.next();

            if (command == 1) {
                System.out.println(limiter.checkRateLimit(clientId));
            } 
            else if (command == 2) {
                System.out.println(limiter.getRateLimitStatus(clientId));
            }
        }
    }
}
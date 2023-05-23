package org.example.websocket.service;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPooled;

public class RandomSyncService {

    private static final String LOWER_BOUND = "lowerBound";
    private final JedisPooled jedis;
    private static final Logger logger = LogManager.getLogger(RandomSyncService.class);

    private final Random random = new Random();
    private final int randomizeInterval = 1000;

    /**
     * Current value stored in Redis.
     * It allows live through reboot and gives multi node synchronization.
     *
     */
    private long lowerBound;

    // CopyOnWriteArraySet is not enough for synchronization between method calls
    private static final HashSet<BigInteger> occupiedIds = new HashSet<>();

    public RandomSyncService(JedisPooled jedis) {
        this.jedis = jedis;
        lowerBound = getBoundFromRedis();
        saveBoundToRedis(lowerBound + randomizeInterval);
    }

    public int getRandomizeInterval() {
        return randomizeInterval;
    }

    public long getBoundFromRedis() {
        String actualBound = jedis.get(LOWER_BOUND);
        logger.debug("BoundFromRedis: {}", actualBound);
        if(actualBound == null){
            return 0;
        }
        return Long.parseLong(actualBound);
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public void saveBoundToRedis(long newLowerBound){
        jedis.set(LOWER_BOUND, String.valueOf(newLowerBound));
    }

    public BigInteger generateSyncronizedId() {
        BigInteger id = randomValWithBounds();
        int i = 1;
        synchronized (occupiedIds) {
            while (occupiedIds.contains(id)) { // generate new Id, while we do not find free value
                //begin the new Interval of random digits
                if (i > randomizeInterval / 2) {  // when new random digits has match the half of occupiedIds
                    lowerBound = getBoundFromRedis();
                    saveBoundToRedis(lowerBound + randomizeInterval); //multi node synchronization
                    occupiedIds.clear();
                }
                i++;
                id = randomValWithBounds();
            }
            //we need synchronized between .contains and .add method calls
            occupiedIds.add(id);
        }
        logger.debug("generateNewVal called {} times", i);
        if(id.longValue() < lowerBound){
            logger.error("Incorrect id ! lowerBound: {}, randomizeInterval: {}",
                lowerBound, randomizeInterval);
        }
        return id;
    }

    private BigInteger randomValWithBounds() {
        return BigInteger.valueOf(lowerBound + random.nextInt(randomizeInterval));
    }
}

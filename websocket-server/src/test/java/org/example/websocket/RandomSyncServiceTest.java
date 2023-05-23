package org.example.websocket;

import java.math.BigInteger;
import org.example.websocket.service.RandomSyncService;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class RandomSyncServiceTest {

    RandomSyncService randomSyncService = new RandomSyncService();

    @Test
    public void generateSyncronizedId() {
        long lowerBound = randomSyncService.getLowerBound();
        BigInteger newId = randomSyncService.generateSyncronizedId();
        assertTrue(newId.longValue() >= lowerBound);
    }
}
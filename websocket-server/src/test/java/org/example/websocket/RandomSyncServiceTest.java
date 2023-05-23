package org.example.websocket;

import java.math.BigInteger;
import org.example.websocket.service.RandomSyncService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import redis.clients.jedis.JedisPooled;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RandomSyncServiceTest {
    @Mock
    private JedisPooled jedis;
    @InjectMocks
    RandomSyncService randomSyncService;

    @Test
    @DisplayName("Проверяем что новый ID больше, чем lowerBound. И в Редис записали lowerBound + randomSyncService.getRandomizeInterval()")
    public void generateSyncronizedId() {
        long lowerBound = randomSyncService.getLowerBound();
        BigInteger newId = randomSyncService.generateSyncronizedId();
        assertTrue(newId.longValue() >= lowerBound);
        verify(jedis, times(1)).get(anyString());
        when(jedis.get(anyString()))
            .thenReturn(String.valueOf(randomSyncService.getRandomizeInterval()));
        assertEquals(lowerBound + randomSyncService.getRandomizeInterval(), randomSyncService.getBoundFromRedis());
    }

    @Test
    @DisplayName("Проверяем что lowerBound увеличивается и записывается в Redis при кол-ве запросов > RandomizeInterval")
    public void testLowerBoundIncrease() {
        long origin = randomSyncService.getLowerBound();
        when(jedis.get(anyString()))
            .thenReturn(String.valueOf(randomSyncService.getRandomizeInterval()));
        for(long i=0; i <= randomSyncService.getRandomizeInterval()+1; i++){
            randomSyncService.generateSyncronizedId();
        }
        assertEquals(origin + randomSyncService.getRandomizeInterval(), randomSyncService.getLowerBound());
        //1 time - saving in constructor and second time - on increase of RandomizeInterval
        verify(jedis, times(2)).set(anyString(), anyString());
    }
}
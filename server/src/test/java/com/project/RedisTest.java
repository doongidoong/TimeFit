package com.project;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testFairLock() throws InterruptedException {
        //given
        String lockName = "testLock";
        List<Long> threadOrder = new ArrayList<>();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(()-> {
                RLock fairLock = redissonClient.getLock(lockName);
                try {
                    boolean available = fairLock.tryLock(25, 20, TimeUnit.SECONDS);
                    log.info("Thread is "+Thread.currentThread().getName());
                    Thread.sleep(100);
                    if(!available){
                        log.info("락 획득 실패");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    log.info(Thread.currentThread().getName()+" unlock");
                    threadOrder.add(Thread.currentThread().getId()); //락이 해제될 때 리스트에 추가함
                    fairLock.unlock();
                }
            });
        }
        //when
        for (int i = 0; i < 10; i++) {
            Thread.sleep(30);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
        //then
        log.info(threadOrder.toString());
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(threads[i].getId(), threadOrder.get(i));
        }
    }

    @Test
    public void threadTest() throws Exception{
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(()-> {
                log.info(Thread.currentThread().getName());
            });
        }
        for (int i = 1; i < 5; i++) {
            Thread.sleep(100);
            threads[i].start();
            //threads[i].join();
        }
        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }
    }
}

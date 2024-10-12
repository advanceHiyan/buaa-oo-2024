import com.oocourse.TimableOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database<K,V> {
    private final Map<K,V> map = new HashMap<>();
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true /* fair */);
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    
    // 给key分配value
    public void insert(K key, V value) {
        //TODO
        //请替换sentence1为合适内容(1)
        writeLock.lock();
        try {
            verySlowly();
            if (!map.containsKey(key)) {
                map.put(key, value);
                TimableOutput.println(Thread.currentThread().getName() + ":insert(" + key + ", " + value + ")");
            } else {
                TimableOutput.println(Thread.currentThread().getName() + ":insert failed");
            }
        } finally {
            //TODO
            //请替换sentence2为合适内容(2)
            writeLock.unlock();
        }
    }

    // 更新key对应value
    public void replace(K key, V value) {
        //TODO
        //请替换sentence3为合适内容(3)
        writeLock.lock();
        try {
            normalSlowly();
            if (map.containsKey(key)) {
                map.replace(key, value);
                TimableOutput.println(Thread.currentThread().getName() + ":replace(" + key + ", " + value + ")");
            } else {
                TimableOutput.println(Thread.currentThread().getName() + ":replace failed");
            }
        } finally {
            //TODO
            //请替换sentence4为合适内容(4)
            writeLock.unlock();
        }
    }
    
    // 获取给key分配的值
    public V retrieve(K key) {
        //TODO
        //请替换sentence5为合适内容(5)
        readLock.lock();
        try {
            slowly();
            return map.get(key);
        } finally {
            //TODO
            //请替换sentence6为合适内容(6)
            readLock.unlock();
        }
    }
    
    // 模拟耗时的操作
    private void slowly() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 模拟非常耗时的操作
    private void normalSlowly() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // 模拟非常耗时的操作
    private void verySlowly() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package MyMath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有关测试LRU 算 法
 * 利用LinkedHashMap实现简单的缓存 ， 必须实现removeEldestEntry方法，具体参见JDK文档
 */


public class LruLinkedHashMap<k,v> extends LinkedHashMap<k,v> {
    private final int maxCapacity;
    private static final float DEFAULT_LOAL_FACTOR=0.75f;
    private final Lock lock = new ReentrantLock();


    public LruLinkedHashMap(int maxCapacity) {
        super(maxCapacity,DEFAULT_LOAL_FACTOR,true);
        this.maxCapacity = maxCapacity;
    }

    //源码是 return false;
    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<k,v> eldest){
        return  size()>maxCapacity;
    }

    @Override
    public boolean containsKey(Object key){
        try {
            lock.lock();
            return super.containsKey(key);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public v get(Object key){
        try {
            lock.lock();
            return super.get(key);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public v put(k key,v value){
        try {
            lock.lock();
            return super.put(key,value);
        }finally {
            lock.unlock();
        }
    }

    public int size(){
        try {
            lock.lock();
            return super.size();
        }finally {
            lock.unlock();
        }
    }

    public void clear(){
        try {
            lock.lock();
            super.clear();
        }finally {
            lock.unlock();
        }
    }

    public Collection<Map.Entry<k,v>> getAll(){
        try {
            lock.lock();
            return new ArrayList<Map.Entry<k, v>>(super.entrySet());
        }finally {
            lock.unlock();
        }
    }
}









































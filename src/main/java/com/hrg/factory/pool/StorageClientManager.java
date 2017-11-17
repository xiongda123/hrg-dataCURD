package com.hrg.factory.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/11/14.
 */
public class StorageClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientManager.class);
    private static boolean init;
    private static StorageClientPool pool;

    public static void initPool(int minSize,int maxSize) {
        if (init) {
            LOGGER.error("请勿重复初始化本类.");
            return;
        }
        init = true;
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxSize);
        config.setMinIdle(minSize);
        config.setMaxIdle(maxSize);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        PooledStorageClientFactory factory = new PooledStorageClientFactory();
        pool = new StorageClientPool(factory, config);
    }

    /**
     * 获取连接
     *
     * @return
     */
    public static StorageClient getStorageClient() {
        LOGGER.info(Thread.currentThread().getName()+" get storage client() !");
        StorageClient client = null;
        try {
            check();
            client = pool.borrowObject();
        } catch (Exception e) {
            LOGGER.error("Unable to borrow StorageClient from pool", e);
        }
        return client;
    }

    /**
     * 返还连接
     *
     * @param client
     * @return
     */
    public static void returnStorageClient(StorageClient client) {
        LOGGER.info(Thread.currentThread().getName()+" return storage client() !");
        try {
            check();
            pool.returnObject(client);
        } catch (Exception e) {
            LOGGER.error("Unable to return StorageClient", e);
        }
    }

    static void check() throws Exception {
        if (pool == null) {
            throw new Exception("请先调用init方法初始化本类");
        }
    }

    public static void main(String[] args) throws Exception {
        ClientGlobal.init("F:\\fdfs-client.conf");
        StorageClientManager.initPool(2, 10);

        new Thread(new Runnable() {
            public void run() {
                startTest();
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                startTest();
            }
        }).start();

    }

    private static void startTest(){
        while (true) {
            StorageClient client = null;
            try {
                client = StorageClientManager.getStorageClient();
                LOGGER.info(Thread.currentThread().getName()+": create client hashCode: " + client.hashCode());
            } finally {
                LOGGER.info(Thread.currentThread().getName()+": return client hashCode: " + client.hashCode());
                StorageClientManager.returnStorageClient(client);
            }
            try {
                Thread.sleep(2000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

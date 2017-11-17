package com.hrg.factory.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.StorageClient;

/**
 * Created by Administrator on 2017/11/14.
 */
public class StorageClientPool extends GenericObjectPool<StorageClient>{
    public StorageClientPool(PooledStorageClientFactory factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }


}

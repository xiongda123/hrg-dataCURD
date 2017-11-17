package com.hrg.factory.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * Created by Administrator on 2017/11/14.
 */
class PooledStorageClientFactory extends BasePooledObjectFactory<StorageClient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PooledStorageClientFactory.class);
    @Override
    public StorageClient create() throws Exception {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = null;
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
//            InetSocketAddress address = trackerServer.getInetSocketAddress();
//            trackerServer.getSocket().connect(address, ClientGlobal.g_connect_timeout);
            return storageClient;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PooledObject<StorageClient> wrap(StorageClient server) {
        return new DefaultPooledObject<StorageClient>(server);
    }
}

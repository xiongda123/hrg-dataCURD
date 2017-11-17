package com.hrg.service;

import com.hrg.dao.FdfsManager;
import com.hrg.domain.FdfsFile;
import com.hrg.factory.pool.StorageClientManager;
import org.csource.fastdfs.StorageClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by xiongzy on 2017/11/15.
 */
public class FdfsServiceImpl {

    /**
     * 上传文件到FastDFS文件系统,并创建元数据的Elasticsearch索引
     * @param fdfsFile {@link FdfsFile}
     * @param createIndex 是否在elasticsearch上创建索引
     */
    public static boolean uploadFile(FdfsFile fdfsFile, boolean createIndex) {
        StorageClient client = null;
        try{
            client = StorageClientManager.getStorageClient();
            FdfsManager.uploadFile(client, fdfsFile);
            if(fdfsFile.getPath() != null && createIndex){
                createIndex(fdfsFile);
                return true;
            } else {
                return false;
            }
        }finally {
            if(client != null){
                StorageClientManager.returnStorageClient(client);
            }
        }
    }

    /**
     *从fastDFS中下载数据
     * @param fdfsFile {@link FdfsFile}
     * @return
     */
    public static boolean downloadFile(FdfsFile fdfsFile) {
        StorageClient client = null;
        try{
            client = StorageClientManager.getStorageClient();
            FdfsManager.downloadFile(client, fdfsFile);
        } finally {
            if(client != null){
                StorageClientManager.returnStorageClient(client);
            }
        }
        return false;
    }

    /**
     * 删除fastDFS中的文件
     * @param fdfsFile
     * @return
     */
    public static boolean deleteFile(FdfsFile fdfsFile){
        StorageClient client = null;
        try{
            client = StorageClientManager.getStorageClient();
            FdfsManager.deleteFile(client, fdfsFile);
        } finally {
            if(client != null){
                StorageClientManager.returnStorageClient(client);
            }
        }
        return false;
    }

    private static void createIndex(FdfsFile fdfsFile) {
        ESServiceImpl.createIndex(fdfsFile.getIndex(), fdfsFile.getType(), createJson(fdfsFile));
    }

    /**
     * 使用es的帮助类
     */
    public static XContentBuilder createJson(FdfsFile fdfsFile){

        // 创建json对象, 其中一个创建json的方式
        XContentBuilder source = null;
        try {
            source = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("time", fdfsFile.getTime())
                    .field("description", fdfsFile.getDescription())
                    .field("path", fdfsFile.getPath())
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }
}

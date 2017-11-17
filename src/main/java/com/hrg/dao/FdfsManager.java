package com.hrg.dao;

import com.hrg.domain.FdfsFile;
import com.hrg.global.UserGlobal;
import org.csource.common.MyException;
import org.csource.fastdfs.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by xiongzy on 2017/11/15.
 */
public class FdfsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdfsManager.class);

    public static boolean uploadFile(StorageClient client, FdfsFile fdfsFile) {
        try {
            String[] uploadResults = client.upload_file(fdfsFile.getContent(), fdfsFile.getExt(), fdfsFile.getMetaData());
            if (uploadResults == null) {
                LOGGER.error("upload file fail, error code: " + client.getErrorCode());
            }else{
                fdfsFile.setGroupName(uploadResults[0]);
                fdfsFile.setPath(UserGlobal.HTTP_PREFIX + uploadResults[0] + "/" + uploadResults[1]);
                LOGGER.info("upload file successfully!!!  file path : " + fdfsFile.getPath());
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean downloadFile(StorageClient client, FdfsFile fdfsFile) {
        try {
            byte[] content = client.download_file(fdfsFile.getGroupName(), fdfsFile.getPath());
            if(content == null){
                LOGGER.error("download file fail, error code: " + client.getErrorCode());
            } else {
                fdfsFile.setContent(content);
                LOGGER.info("download file successfully!!!");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFile(StorageClient client, FdfsFile fdfsFile) {
        try {
            int result = client.delete_file(fdfsFile.getGroupName(), fdfsFile.getPath());
            if(result == 0){
                LOGGER.info("delete file successfully!!! file path: "+fdfsFile.getPath());
                return true;
            } else {
                LOGGER.error("download file fail, error code: " + client.getErrorCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return false;
    }
}

package com.hrg.test;

import com.hrg.domain.FdfsFile;
import com.hrg.service.FdfsServiceImpl;
import com.hrg.global.UserGlobal;
import com.hrg.utils.TimeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/14.
 */
public class FdfsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FdfsTest.class);

    public static void main(String[] args) throws Exception {
        UserGlobal.parmInit();
        testUploadFile();
//        testDownloadFile();
//        deleteFile();
    }

    private static void deleteFile() {
        new DeleteThread().start();
    }

    private static class DeleteThread extends Thread {
        @Override
        public void run() {
            FdfsFile fdfsFile = new FdfsFile();
            fdfsFile.setIndex("hrg");
            fdfsFile.setType("pic");
            fdfsFile.setGroupName("group1");
            fdfsFile.setPath("M00/00/00/wKgB7VoAFtqAVuVMAAAqdBZBVr8301.jpg");
            FdfsServiceImpl.deleteFile(fdfsFile);
        }
    }

    private static void testDownloadFile() {
        new DownloadThread().start();
    }

    private static void testUploadFile() {
        new UploadThread().start();
    }

    private static class DownloadThread extends Thread {
        @Override
        public void run() {
            FdfsFile fdfsFile = new FdfsFile();
            fdfsFile.setIndex("hrg");
            fdfsFile.setType("pic");
            fdfsFile.setGroupName("group1");
            fdfsFile.setPath("M00/00/00/wKgB7VoL_5qAB5r-AAAqdBZBVr8250.jpg");
            FdfsServiceImpl.downloadFile(fdfsFile);
        }
    }

    private static class UploadThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                FdfsFile fdfsFile = new FdfsFile();
                fdfsFile.setIndex("hrg");
                fdfsFile.setType("pic");
                fdfsFile.setExt("jpg");
                fdfsFile.setDescription("null");
                fdfsFile.setTime(TimeTool.format(TimeTool.yyyyMMddHHmmss, new Date()));
                fdfsFile.setContent(file_buff);
                FdfsServiceImpl.uploadFile(fdfsFile, true);
            }
        }
    }

    public static byte[] file_buff;

    static {
        FileInputStream fis = null;
        File file = null;
        try {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) {
                file = new File("F:/1.jpg");
            } else {
                file = new File("/1.jpg");
            }

            fis = new FileInputStream(file);
            if (fis != null) {
                int len = fis.available();
                file_buff = new byte[len];
                fis.read(file_buff);
            }
        } catch (Exception e) {

        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

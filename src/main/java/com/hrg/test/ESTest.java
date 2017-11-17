package com.hrg.test;

import com.hrg.domain.FdfsFile;
import com.hrg.global.UserGlobal;
import com.hrg.service.ESServiceImpl;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xiongzy on 2017/11/13.
 */
public class ESTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESTest.class);

    @Before
    public void initGlobleParams() {
        UserGlobal.parmInit();
    }

    @Test
    public void testCreateIndex() {
        //插入数据(数据量大时,最好使用批量插入,此处为单条插入)
//        try {
//            for (int i = 1; i <= 1000; i++) {
//                ESServiceImpl.createIndex("test5", "test5", "id" + i, createJson3("name" + i, i + ""));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ESServiceImpl.createIndex("hrg", "pic", createJson1());

    }

    @Test
    public void testDeleteIndex() {
        for (int i = 0; i < 1; i++) {
            ESServiceImpl.deleteIndex("test4", "test4", "1");
        }
    }

    @Test
    public void testDeleteIndexAll() {
        ESServiceImpl.deleteIndexALL("hrg");
    }

    @Test
    public void testSearch() {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("", "");
        List<FdfsFile> fileList = ESServiceImpl.search(null, "hrg", "pic");
        Iterator<FdfsFile> iter = fileList.iterator();
        while (iter.hasNext()){
            FdfsFile fdfsFile = iter.next();
            LOGGER.info("search file =" + fdfsFile.toString());
        }
    }

    @Test
    public void testScrolls() {
//        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("user", "kimchy");
        List<FdfsFile> fileList = ESServiceImpl.scrollSearch("hrg", "pic", null);
        Iterator<FdfsFile> iter = fileList.iterator();
        while (iter.hasNext()){
            FdfsFile fdfsFile = iter.next();
            LOGGER.info("search file =" + fdfsFile.toString());
        }
    }

    /**
     * 组织json串, 方式1,直接拼接
     */
    public static String createJson1() {
        String json = "{" +
                "\"user\":\"xiong\"," +
                "\"postDate\":\"2017-11-13\"," +
                "\"message\":\"hello\"" +
                "}";
        return json;
    }

    /**
     * 使用es的帮助类
     */
    public XContentBuilder createJson2() throws Exception {
        // 创建json对象, 其中一个创建json的方式
        XContentBuilder source = XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "kimchy")
                .field("postDate", new Date())
                .field("message", "trying to out ElasticSearch")
                .endObject();
        return source;
    }

    public XContentBuilder createJson3(String name, String id) throws Exception {
        // 创建json对象, 其中一个创建json的方式
        XContentBuilder source = XContentFactory.jsonBuilder()
                .startObject()
                .field("name", name)
                .field("id", id)
                .endObject();
        return source;
    }


}

package com.hrg.domain;

import com.alibaba.fastjson.JSON;
import org.csource.common.NameValuePair;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;

/**
 * Created by xiongzy on 2017/11/15.
 */
public class FdfsFile {
    private String index;//索引名称，相当于数据库名称
    private String type;//索引类型，相当于数据库中的表名
    private String groupName;//文件在FastDFS中所属组名
    private String path;//FastDFS下的文件路径
    private String ext;//文件后缀名
    private String description;//文件的简要描述
    private String time;//文件生成时间
    private byte[] content;//文件字节内容
    private NameValuePair[] metaData;//文件元数据。可以设置一些键值对保存文件的元数据。 NameValuePair pair1 = new NameValuePair(key,value)

    public FdfsFile(){}

    public FdfsFile(SearchHit hit){
        this.time = (String) hit.getSource().get("time");
        this.description = (String) hit.getSource().get("description");
        this.path = (String) hit.getSource().get("path");
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public NameValuePair[] getMetaData() {
        return metaData;
    }

    public void setMetaData(NameValuePair[] metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "FdfsFile{" +
                "time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}

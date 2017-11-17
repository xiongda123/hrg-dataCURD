package com.hrg.dao;

import com.hrg.domain.FdfsFile;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xiongzy on 2017/11/16.
 */
public class ESManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESManager.class);
    /**
     * 创建索引
     *
     * @param index
     *            索引名称，相当于数据库名称
     * @param type
     *            索引类型，相当于数据库中的表名
     * @param json
     *            json数据
     */
    public static void createIndex(BulkProcessor bulkProcessor, String index, String type, String json) {
        if (bulkProcessor == null) {
            LOGGER.error("client can not be null !!!");
            return;
        }
        bulkProcessor.add(new IndexRequest(index, type).source(json, XContentType.JSON));
    }

    /**
     * 创建索引
     *
     * @param index
     *            索引名称，相当于数据库名称
     * @param type
     *            索引类型，相当于数据库中的表名
     * @param source
     *            json数据
     */
    public static void createIndex(BulkProcessor bulkProcessor, String index, String type, XContentBuilder source) {
        if (bulkProcessor == null) {
            LOGGER.error("client can not be null !!!");
            return;
        }
        bulkProcessor.add(new IndexRequest(index, type).source(source));
    }

    /**
    * 删除索引
    *
    * @param index 索引名称，相当于数据库名称
    * @param type 索引类型，相当于数据库中的表名
    * @param id 索引ID
    */
    public static void deleteIndex(BulkProcessor bulkProcessor, String index, String type, String id) {
        if (bulkProcessor == null) {
            LOGGER.error("client can not be null !!!");
            return;
        }
        bulkProcessor.add(new DeleteRequest(index, type, id));
    }

    /**
     * 删除索引,索引下的记录全部删除，请谨慎使用
     *
     * @param index
     *            索引名称，相当于数据库名称
     * @return
     */
    public static DeleteIndexResponse deleteIndexALL(TransportClient client, String index) {
        if (client == null) {
            LOGGER.error("client can not be null !!!");
            return null;
        }
        DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(index).get();
        return deleteIndexResponse;
    }

    /**
     * 普通搜索,只能搜索少量的数据，不支持搜索大量的数据
     *
     * @param queryBuilder
     *            一个基本查询条件，例如QueryBuilders.prefixQuery(field, value)
     * @param type
     *            索引类型，相当于数据库中的表名
     * @param index
     *            索引名称，相当于数据库名称
     * @return
     */
    public static void search(TransportClient client, QueryBuilder queryBuilder, String index, String type, List<FdfsFile> list) {
        if (client == null) {
            LOGGER.error("client can not be null !!!");
            return;
        }
        SearchRequestBuilder searchBuilder = client.prepareSearch(index).setTypes(type);
        if (queryBuilder != null) {
            searchBuilder.setQuery(queryBuilder);
        }
        SearchResponse response = searchBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
                .setExplain(true) //explain为true表示根据数据相关度排序，和关键字匹配最高的排在前面
                .get();
        SearchHit[] hits = response.getHits().getHits();    //查询的结果都在hits里面

        for(SearchHit hit : hits){
            FdfsFile fdfsFile = new FdfsFile(hit);
            list.add(fdfsFile);
        }
    }

    /**
     * scroll搜索
     *  @param index
     *         索引名称，相当于数据库名称
     * @param type
     *         索引类型，相当于数据库中的表名
     * @param size
     *         每一次Scroll取出的数据记录数，不能超过10000
     * @param timeout
     *         保持搜索的上下文环境多长时间（滚动时间）
     * @param sortField
     *         排序字段 SortParseElement.DOC_FIELD_NAME
     * @param order
     *         排序方式SortOrder.ASC升序,SortOrder.DESC降序
     * @param queryBuilder
     */

    public static void scrollSearch(TransportClient client, String index, String type, int size, long timeout, String sortField, SortOrder order, QueryBuilder queryBuilder, List<FdfsFile> list) {
        if (client == null) {
            LOGGER.error("client can not be null !!!");
            return;
        }
        SearchRequestBuilder searchBuilder = client.prepareSearch(index).setTypes(type)
                .setSize(size)
                .setScroll(new TimeValue(timeout));
        if (sortField != null && order != null) {
            searchBuilder.addSort(sortField, order);
        }
        if (queryBuilder != null) {
            searchBuilder.setQuery(queryBuilder);
        }
        SearchResponse response = searchBuilder.get();

        Set<String> scrollIdSet = new HashSet<String>();
        List<String> scrollIdList = new ArrayList<String>();

        while (true) {
            for (SearchHit hit : response.getHits().getHits()) {
                FdfsFile fdfsFile = new FdfsFile(hit);
                list.add(fdfsFile);
            }
            String scrollId = response.getScrollId();
            SearchResponse response2 = client.prepareSearchScroll(scrollId)
                    .setScroll(new TimeValue(timeout)).get();
            scrollIdSet.add(scrollId);

            if (response2.getHits().getHits().length == 0) {
                LOGGER.error("no data !");
                break;
            }
        }
        scrollIdList.addAll(scrollIdSet);
        if (scrollIdList.size() > 0) {
            clearScroll(client, scrollIdList);
        }
    }

    /**
     * 清除responce中保存的滚动ID
     *
     * @param client
     * @param scrollIdList
     * @return
     */
    public static boolean clearScroll(Client client, List<String> scrollIdList) {
        ClearScrollRequestBuilder clearScrollRequestBuilder = client.prepareClearScroll();
        clearScrollRequestBuilder.setScrollIds(scrollIdList);
        ClearScrollResponse response = clearScrollRequestBuilder.get();
        return response.isSucceeded();
    }
}

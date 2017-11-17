package com.hrg.service;

import com.hrg.factory.ESClientFactory;
import com.hrg.dao.ESManager;
import com.hrg.domain.FdfsFile;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by xiongzy on 2017/11/13.
 */
public class ESServiceImpl{
    private static final Logger LOGGER = LoggerFactory.getLogger(ESServiceImpl.class);
    private static boolean init;
    //FutureTask结果返回等待超时时间(秒)
    private static int taskTimeout;
    //scroll id的失效时间(毫秒)
    public static int scrollTimeout;
    private static BulkProcessor bulkProcessor;
    private static ExecutorService executor;

    /**
     * 初始化搜索任务线程池，Elasticsearch批处理bulkProcessor实例
     *
     * @param poolSize           搜索任务的执行线程池大小
     * @param interval           固定刷新频率
     * @param concurrentRequests 并发请求数量, 0不并发, 1 并发执行线程池大小
     * @param taskTimeout        查询操作超时时间，秒。为0表示一直等待
     * @param scrollTimeout      scroll id的失效时间,秒
     */
    public static void init(int poolSize, int interval, int concurrentRequests, int taskTimeout, int scrollTimeout) {
        if (init) {
            LOGGER.error("请勿重复初始化本类.");
            return;
        }
        init = true;
        executor = Executors.newFixedThreadPool(poolSize);
        bulkProcessor = BulkProcessor.builder(ESClientFactory.getTransportClient(), new BulkProcessor.Listener() {
            public void beforeBulk(long executionId, BulkRequest request) {
                LOGGER.info("request size:" + request.numberOfActions());
            }

            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                LOGGER.info("failures request:" + response.hasFailures());
            }

            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                LOGGER.error(failure.getMessage(), failure);
            }
        })
                .setBulkActions(1)   // 1次请求执行一次bulk
                .setFlushInterval(TimeValue.timeValueSeconds(interval)) // 固定1s必须刷新一次
                .setConcurrentRequests(concurrentRequests)// 并发请求数量, 0不并发, 1并发允许执行
                .build();

        ESServiceImpl.taskTimeout = taskTimeout;
        ESServiceImpl.scrollTimeout = scrollTimeout * 1000;
    }

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
    public static boolean createIndex(String index, String type, String json) {
        LOGGER.info("index="+index+", type="+type+", json="+json);
        if(bulkProcessor != null){
            ESManager.createIndex(bulkProcessor, index, type, json);
            return true;
        }
        return false;
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
    public static boolean createIndex(String index, String type, XContentBuilder source) {
        LOGGER.info("createIndex(),index="+index+", type="+type+", source="+source.toString());
        if(bulkProcessor != null){
            ESManager.createIndex(bulkProcessor, index, type, source);
            return true;
        }
        return false;
    }

    /**
     * 删除索引
     *
     * @param index
     *            索引名称，相当于数据库名称
     * @param type
     *            索引类型，相当于数据库中的表名
     * @param id
     *            索引ID
     */
    public static boolean deleteIndex(String index, String type, String id) {
        if(bulkProcessor != null){
            ESManager.deleteIndex(bulkProcessor, index, type, id);
            return true;
        }
        return false;
    }

    /**
     * 删除索引,索引下的记录全部删除，请谨慎使用
     *
     * @param index
     *            索引名称，相当于数据库名称
     * @return
     */
    public static boolean deleteIndexALL(final String index) {
        try {
            FutureTask<DeleteIndexResponse> task = new FutureTask<DeleteIndexResponse>(new Callable<DeleteIndexResponse>() {
                @Override
                public DeleteIndexResponse call() throws Exception {
                    TransportClient client = ESClientFactory.getTransportClient();
                    if(client != null){
                        return ESManager.deleteIndexALL(client, index);
                    }
                    return null;
                }
            });
            executor.execute(task);
            DeleteIndexResponse response = task.get(taskTimeout, TimeUnit.SECONDS);
            return response.isAcknowledged();
        } catch (Exception e) {
            return false;
        }
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
    public static List<FdfsFile> search(final QueryBuilder queryBuilder, final String index, final String type){
        try {
            FutureTask<List<FdfsFile>> task = new FutureTask<List<FdfsFile>>(new Callable<List<FdfsFile>>() {
                @Override
                public List<FdfsFile> call() throws Exception {
                    TransportClient client = ESClientFactory.getTransportClient();
                    List<FdfsFile> list = new ArrayList<FdfsFile>();
                    if(client != null){
                        ESManager.search(client, queryBuilder, index, type, list);
                    }
                    LOGGER.info("fileList size =" + list.size());
                    if (list.size() >0){
                        return list;
                    }
                    return null;
                }
            });
            executor.execute(task);
            return task.get(taskTimeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *scroll 搜索，适合搜索大量的数据
     * @param index
     * @param type
     * @param queryBuilder
     */
    public static List<FdfsFile> scrollSearch(String index, String type, QueryBuilder queryBuilder) {
        return scrollSearch(index, type, 500, scrollTimeout, SortParseElement.DOC_FIELD_NAME, SortOrder.ASC, queryBuilder);
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
    public static List<FdfsFile> scrollSearch(final String index, final String type, final int size, final long timeout, final String sortField, final SortOrder order, final QueryBuilder queryBuilder) {
        FutureTask<List<FdfsFile>> task = new FutureTask<List<FdfsFile>>(new Callable<List<FdfsFile>>() {
            @Override
            public List<FdfsFile> call() throws Exception {
                TransportClient client = ESClientFactory.getTransportClient();
                List<FdfsFile> list = new ArrayList<FdfsFile>();
                if(client != null){
                    ESManager.scrollSearch(client, index, type, size, timeout, sortField, order, queryBuilder, list);
                }
                LOGGER.info("fileList size =" + list.size());
                if (list.size() > 0){
                    return list;
                } else {
                    return null;
                }
            }
        });
        executor.execute(task);
        try {
            return task.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
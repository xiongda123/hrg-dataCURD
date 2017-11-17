package com.hrg.global;

import com.hrg.service.ESServiceImpl;
import com.hrg.factory.pool.StorageClientManager;
import org.csource.fastdfs.ClientGlobal;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by xiongzy on 2017/11/13.
 */
public class UserGlobal {

    /**
     * Fast DFS文件的url前缀
     */
    public static String			HTTP_PREFIX							= "http://192.168.1.237:8888/";
    /**
     * elasticsearch 客户端端口
     */
    public static int				ELASTICSEARCH_PORT					= 9300;
    /**
     * elasticsearch 集群节点集合
     */
    public static String[]			ELASTICSEARCH_NODES					= {"slave2","slave3","slave4"};
    /**
     * elasticsearch 集群名称
     */
    public static String			ELASTICSEARCH_CLUSTERNAME			= "HRG_bigdata";
    /**
     * elasticsearch 配置
     */
    public static Settings          ELASTICSEARCH_SETTINGS				= null;
    /**
     * 搜索任务的执行线程池大小
     */
    public static int				ELASTICSEARCH_POOLSIZE				= 1;
    /**
     * 固定刷新频率
     */
    public static int				ELASTICSEARCH_INTERVAL				= 1;
    /**
     * 并发请求数量, 0不并发, 1 并发执行线程池大小
     */
    public static int				ELASTICSEARCH_CONCURRENT_REQUESTS	= 2;
    /**
     * 查询操作超时时间，秒。为0表示一直等待
     */
    private static int				ELASTICSEARCH_TASKTIMEOUT			= 60;
    /**
     * scroll id的失效时间,秒
     */
    private static int				ELASTICSEARCH_SCROLLTIMEOUT			= 60;
    /**
     * tracker的连接池最小大小
     */
    private static int				FDFS_TRACKERMINPOOLSIZE				= 2;
    /**
     * tracker的连接池最大大小
     */
    private static int				FDFS_TRACKERMAXPOOLSIZE				= 10;
    /**
     * storage的连接池大小
     */
    private static int				FDFS_STORAGEPOOLSIZE				= 10;
    /**
     * 集群storage的个数
     */
    private static int				FDFS_STORAGENUMBERS					= 2;


    private static boolean init = false;
    private static final Logger LOG = LoggerFactory.getLogger(UserGlobal.class);
    public static void parmInit() {
        if (init)
            return;
        init = true;

        try {
            ELASTICSEARCH_SETTINGS = Settings.builder().put("cluster.name", ELASTICSEARCH_CLUSTERNAME).build();
            ESServiceImpl.init(ELASTICSEARCH_POOLSIZE, ELASTICSEARCH_INTERVAL, ELASTICSEARCH_CONCURRENT_REQUESTS,
                    ELASTICSEARCH_TASKTIMEOUT, ELASTICSEARCH_SCROLLTIMEOUT);

            ClientGlobal.init("F:\\fdfs-client.conf");
            StorageClientManager.initPool(FDFS_TRACKERMINPOOLSIZE, FDFS_TRACKERMAXPOOLSIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.hrg.factory;

import com.hrg.global.UserGlobal;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by xiongzy on 2017/11/13.
 */
public class ESClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESClientFactory.class);
    private static ThreadLocal<TransportClient> transportClientTL = new ThreadLocal<TransportClient>(){
        @Override
        protected TransportClient initialValue() {
            TransportClient transportClient = createTransportClient();
            return transportClient;
        }
    };

    private static TransportClient createTransportClient() {
        TransportClient client = TransportClient.builder().settings(UserGlobal.ELASTICSEARCH_SETTINGS).build();
        for (String node : UserGlobal.ELASTICSEARCH_NODES) {
            try {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(node), UserGlobal.ELASTICSEARCH_PORT));
            } catch (Exception e) {
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("Exception:" + node, e);
                }
            }
        }
        return client;
    }

    /**
     * ��ȡ�̱߳��ر���TransportClient
     * @return
     */
    public static TransportClient getTransportClient() {
        return transportClientTL.get();
    }
}

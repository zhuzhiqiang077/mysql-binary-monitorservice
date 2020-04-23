package com.yy.fastcustom.mysql.binarylogservice;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.yy.fastcustom.mysql.binarylogservice.dto.JsonObject2MysqlBinaryLogObjectHolder;
import com.yy.fastcustom.mysql.binarylogservice.listener.AggregationListenerGroup;
import com.yy.fastcustom.mysql.binarylogservice.listener.BasicDataExportProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by zzq.
 */
@Slf4j
//@Component
public class MysqlBinaryLogMonitor {
    private BinaryLogClient client;

    public void connect() {

        new Thread(() -> {
            client = new BinaryLogClient(
                    "localhost",
                    3306,
                    "root",
                    "123456"
            );
            client.setBinlogFilename("binlog-zzq.000426");
            client.setBinlogPosition(0L);

            JsonObject2MysqlBinaryLogObjectHolder jsonObject2MysqlBinaryLogObjectHolder = new JsonObject2MysqlBinaryLogObjectHolder();
            BasicDataExportProcessor basicDataExportProcessor = new KafkaDataExportProcessor();
            AggregationListenerGroup listenerGroup = new AggregationListenerGroup(jsonObject2MysqlBinaryLogObjectHolder, basicDataExportProcessor);
            listenerGroup.registerMetaData();//将静态的表结构绑定到其中

            client.registerEventListener(listenerGroup);

            try {
                log.info("connecting to binarylogservice start");
                client.connect();
                log.info("connecting to binarylogservice done");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }).start();
    }

    public void close() {
        try {
            client.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

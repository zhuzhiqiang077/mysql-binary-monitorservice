package com.yy.fastcustom.mysql.runner;

import com.yy.fastcustom.mysql.binarylogservice.MysqlBinaryLogMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Qinyi.
 */
@Slf4j
@Component
public class BinlogRunner implements CommandLineRunner {

    private final MysqlBinaryLogMonitor client;

    @Autowired
    public BinlogRunner(MysqlBinaryLogMonitor client) {
        this.client = client;
    }

    public static void main(String[] args) {
        MysqlBinaryLogMonitor mysqlBinaryLogMonitor = new MysqlBinaryLogMonitor();
        mysqlBinaryLogMonitor.connect();
    }

    @Override
    public void run(String... strings) throws Exception {

        log.info("Coming in BinlogRunner...");
        client.connect();
    }
}

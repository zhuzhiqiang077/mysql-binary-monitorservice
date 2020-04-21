package com.yy.fastcustom.mysql.binarylogservice;

import com.yy.fastcustom.mysql.binarylogservice.dto.MysqlRowData;
import com.yy.fastcustom.mysql.binarylogservice.listener.BasicDataExportProcessor;

/**
 * Created by zzq on 2020/4/21/021.
 */
public class KafkaDataExportProcessor extends BasicDataExportProcessor {
    @Override
    public void export1(MysqlRowData mysqlRowData) {
        System.out.println(mysqlRowData.toString());
    }
}

package com.yy.fastcustom.mysql.binarylogservice.listener;

import com.github.shyiko.mysql.binlog.event.EventType;

import com.yy.fastcustom.mysql.binarylogservice.constant.*;
import com.yy.fastcustom.mysql.binarylogservice.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzq.
 */
@Slf4j
public abstract class BasicDataExportProcessor implements DataExportProcessor {

    @Override
    public void export0(BinlogRowData BinlogRowData) {

        MysqlBinaryLogDataTableObject table = BinlogRowData.getTable();
        EventType eventType = BinlogRowData.getEventType();

        // 包装成最后需要投递的数据
        MysqlRowData rowData = new MysqlRowData();
        rowData.setTableName(table.getTableName());
        rowData.setLevel(BinlogRowData.getTable().getLevel());
        OpType opType = OpType.to(eventType);
        rowData.setOpType(opType);
        rowData.setDbName(BinlogRowData.getDbName());
        // 取出模板中该操作对应的字段列表
        List<String> fieldList = table.getOpTypeFieldSetMap().get(opType);
        if (null == fieldList) {
            log.warn("{} not support for {}", opType, table.getTableName());
            return;
        }

        for (Map<String, String> afterMap : BinlogRowData.getAfter()) {

            Map<String, String> _afterMap = new HashMap<>();

            for (Map.Entry<String, String> entry : afterMap.entrySet()) {

                String colName = entry.getKey();
                String colValue = entry.getValue();

                _afterMap.put(colName, colValue);
            }

            rowData.getFieldValueMap().add(_afterMap);
        }

        //最后将解析后的数据，交给下一个服务处理
        export1(rowData);
    }
}

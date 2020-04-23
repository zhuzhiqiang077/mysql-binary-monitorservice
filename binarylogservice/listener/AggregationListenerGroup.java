package com.yy.fastcustom.mysql.binarylogservice.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.yy.fastcustom.mysql.binarylogservice.constant.Constant;
import com.yy.fastcustom.mysql.binarylogservice.dto.BinlogRowData;
import com.yy.fastcustom.mysql.binarylogservice.dto.JsonObject2MysqlBinaryLogObjectHolder;
import com.yy.fastcustom.mysql.binarylogservice.dto.MysqlBinaryLogDataTableObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by zzq.
 */
@Slf4j
public class AggregationListenerGroup implements BinaryLogClient.EventListener {
    private String dbName;
    private String tableName;

    private Map<String, DataExportProcessor> listenerMap = new HashMap<>();

    private final JsonObject2MysqlBinaryLogObjectHolder jsonObject2MysqlBinaryLogObjectHolder;
    private final DataExportProcessor dataExportProcessor;

    public AggregationListenerGroup(JsonObject2MysqlBinaryLogObjectHolder jsonObject2MysqlBinaryLogObjectHolder, DataExportProcessor dataExportProcessor) {
        this.jsonObject2MysqlBinaryLogObjectHolder = jsonObject2MysqlBinaryLogObjectHolder;
        this.dataExportProcessor = dataExportProcessor;
    }

    private String genKey(String dbName, String tableName) {
        return dbName + "." + tableName;
    }

    public void registerMetaData() {
        Constant.table2Db.forEach((tableName, dbName) -> {
                    log.info("registerMetaData : {}-{}", dbName, tableName);
                    this.listenerMap.put(genKey(dbName, tableName), dataExportProcessor);
                }
        );
    }

    @Override
    public void onEvent(Event event) {
        EventType type = event.getHeader().getEventType();
        log.debug("event type: {}", type);

        if (type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            this.tableName = data.getTable();
            this.dbName = data.getDatabase();
            return;
        }

        if (type != EventType.UPDATE_ROWS
                && type != EventType.EXT_WRITE_ROWS
                && type != EventType.EXT_DELETE_ROWS
                && type != EventType.UPDATE_ROWS
                && type != EventType.WRITE_ROWS
                && type != EventType.DELETE_ROWS) {
            return;
        }

        // 表名和库名是否已经完成填充
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
            return;
        }

        // 找出对应表有兴趣的监听器
        String key = genKey(this.dbName, this.tableName);
        DataExportProcessor listener = this.listenerMap.get(key);
        if (null == listener) {
            log.debug("skip {}", key);
            return;
        }

        log.info("trigger event: {}", type.name());

        try {

            BinlogRowData rowData = buildRowData(event.getData());
            if (rowData == null) {
                return;
            }

            rowData.setEventType(type);
            listener.export0(rowData);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        } finally {
            this.dbName = "";
            this.tableName = "";
        }
    }

    private List<Serializable[]> getAfterValues(EventData eventData) {

        if (eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData) eventData).getRows();
        }

        if (eventData instanceof UpdateRowsEventData) {
            return ((UpdateRowsEventData) eventData).getRows().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        if (eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData) eventData).getRows();
        }

        return Collections.emptyList();
    }

    private BinlogRowData buildRowData(EventData eventData) {

        MysqlBinaryLogDataTableObject table = jsonObject2MysqlBinaryLogObjectHolder.getTable(tableName);

        if (null == table) {
            log.warn("table {} not found", tableName);
            return null;
        }

        List<Map<String, String>> afterMapList = new ArrayList<>();

        getAfterValues(eventData).stream().forEach(after -> {
            Map<String, String> afterMap = new HashMap<>();

            int colLen = after.length;

            IntStream.range(0, colLen).forEach(index -> {
                // 取出当前位置对应的列名
                String colName = table.getPosMap().get(index);

                // 如果没有则说明不关心这个列
                if (null == colName) {
                    log.debug("ignore position: {}", index);
                    return;
                }
                String colValue = null;
                Serializable serializableObject = after[index];
                if (serializableObject != null)
                    colValue = after[index].toString();
                afterMap.put(colName, colValue);
            });

            afterMapList.add(afterMap);
        });

        BinlogRowData rowData = new BinlogRowData();
        rowData.setAfter(afterMapList);
        rowData.setTable(table);

        return rowData;
    }
}

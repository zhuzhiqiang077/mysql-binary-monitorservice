package com.yy.fastcustom.mysql.binarylogservice.dto;

import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by zzq.
 */
@Data
public class BinlogRowData {

    private MysqlBinaryLogDataTableObject table;

    private EventType eventType;

    private List<Map<String, String>> after;

    private List<Map<String, String>> before;
}

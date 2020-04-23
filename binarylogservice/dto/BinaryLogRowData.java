package com.yy.fastcustom.mysql.binarylogservice.dto;

import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by zzq.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinaryLogRowData {
    private Long timestamp;

    private Long nextPosition;

    private String dbName;

    private MysqlBinaryLogDataTableObject table;

    private EventType eventType;

    private List<Map<String, String>> after;

    private List<Map<String, String>> before;
}

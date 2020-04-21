package com.yy.fastcustom.mysql.binarylogservice.dto;

import com.yy.fastcustom.mysql.binarylogservice.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzq.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MysqlBinaryLogDataTableObject {

    private String tableName;
    private String level;

    /**
     * 操作方式 -> 涉及的字段名称
     */
    private Map<OpType, List<String>> opTypeFieldSetMap = new HashMap<>();

    /**
     * 字段索引 -> 字段名
     * */
    private Map<Integer, String> posMap = new HashMap<>();
}

package com.yy.fastcustom.mysql.binarylogservice.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzq.
 */
@Data
public class MysqlBinaryLogDataBaseObject {

    private String database;

    private Map<String, MysqlBinaryLogDataTableObject> tableTemplateMap = new HashMap<>();
}

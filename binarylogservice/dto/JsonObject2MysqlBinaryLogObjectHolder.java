package com.yy.fastcustom.mysql.binarylogservice.dto;

import com.alibaba.fastjson.JSON;
import com.yy.fastcustom.mysql.binarylogservice.constant.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.util.function.Consumer;
//import java.util.function.IntConsumer;
import java.util.function.Supplier;
//import java.util.stream.IntStream;

/**
 * Created by zzq.
 */
@Slf4j
public class JsonObject2MysqlBinaryLogObjectHolder {

    private MysqlBinaryLogDataBaseObject mysqlBinaryLogDataBaseObject;
//    private final JdbcTemplate jdbcTemplate;

//    private String SQL_SCHEMA = "select table_schema, table_name, " +
//            "column_name, ordinal_position from information_schema.columns " +
//            "where table_schema = ? and table_name = ?";

//    public JsonObject2MysqlBinaryLogObjectHolder(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    {
        loadJson("template.json");
    }

    public MysqlBinaryLogDataTableObject getTable(String tableName) {
        return mysqlBinaryLogDataBaseObject.getTableTemplateMap().get(tableName);
    }

    private void loadJson(String path) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream inStream = cl.getResourceAsStream(path);

        try {
            DataBaseJsonObject dataBaseJsonObject = JSON.parseObject(
                    inStream,
                    Charset.defaultCharset(),
                    DataBaseJsonObject.class
            );
            mysqlBinaryLogDataBaseObject = parse(dataBaseJsonObject);
//            loadMeta();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException("fail to parse json file");
        }
    }

    public static MysqlBinaryLogDataBaseObject parse(DataBaseJsonObject dataBaseJsonObject) {
        MysqlBinaryLogDataBaseObject template = new MysqlBinaryLogDataBaseObject();
        template.setDatabase(dataBaseJsonObject.getDatabase());

        dataBaseJsonObject.getTableList().stream().forEach(dataTable -> {
            String tableName = dataTable.getTableName();
            Integer level = dataTable.getLevel();

            MysqlBinaryLogDataTableObject mysqlBinaryLogDataTableObject = new MysqlBinaryLogDataTableObject();
            mysqlBinaryLogDataTableObject.setTableName(tableName);
            mysqlBinaryLogDataTableObject.setLevel(level.toString());
            template.getTableTemplateMap().put(tableName, mysqlBinaryLogDataTableObject);

            // 遍历操作类型对应的列
            Map<OpType, List<String>> opTypeFieldSetMap = mysqlBinaryLogDataTableObject.getOpTypeFieldSetMap();
            dataTable.getInsert().stream().forEach(column -> {
                getOrCreate(
                        OpType.ADD,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
                buildPositionColumnMap(mysqlBinaryLogDataTableObject, column);
            });

            dataTable.getUpdate().stream().forEach(column -> {
                getOrCreate(
                        OpType.UPDATE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
                buildPositionColumnMap(mysqlBinaryLogDataTableObject, column);
            });

            dataTable.getDelete().stream().forEach(column -> {
                getOrCreate(
                        OpType.DELETE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
                buildPositionColumnMap(mysqlBinaryLogDataTableObject, column);
            });
        });

        return template;
    }

    private static void buildPositionColumnMap(MysqlBinaryLogDataTableObject mysqlBinaryLogDataTableObject, DataTableJsonObject.Column column) {
        Integer includedColumnsPosition = column.getPosition() - 1;
        mysqlBinaryLogDataTableObject.getPosMap().put(includedColumnsPosition, column.getColumn());
    }

    private static <T, R> R getOrCreate(T key, Map<T, R> map,
                                        Supplier<R> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }

//    private void loadMeta() {
//
//        for (Map.Entry<String, MysqlBinaryLogDataTableObject> entry :
//                template.getTableTemplateMap().entrySet()) {
//
//            MysqlBinaryLogDataTableObject table = entry.getValue();
//
//            List<String> updateFields = table.getOpTypeFieldSetMap().get(
//                    OpType.UPDATE
//            );
//            List<String> insertFields = table.getOpTypeFieldSetMap().get(
//                    OpType.ADD
//            );
//            List<String> deleteFields = table.getOpTypeFieldSetMap().get(
//                    OpType.DELETE
//            );
//
//            jdbcTemplate.query(SQL_SCHEMA, new Object[]{
//                    template.getDatabase(), table.getTableName()
//            }, (rs, i) -> {
//
//                int pos = rs.getInt("ORDINAL_POSITION");
//                String colName = rs.getString("COLUMN_NAME");
//
//                if ((null != updateFields && updateFields.contains(colName))
//                        || (null != insertFields && insertFields.contains(colName))
//                        || (null != deleteFields && deleteFields.contains(colName))) {
//                    table.getPosMap().put(pos - 1, colName);
//                }
//
//                return null;
//            });
//        }
//    }
}

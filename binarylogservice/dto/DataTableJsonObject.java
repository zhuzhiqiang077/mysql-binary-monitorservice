package com.yy.fastcustom.mysql.binarylogservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by zzq.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTableJsonObject {

    private String tableName;
    private Integer level;

    private List<Column> insert;
    private List<Column> update;
    private List<Column> delete;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {

        private String column;
        private Integer position;
    }
}

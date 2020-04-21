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
public class DataBaseJsonObject {

    private String database;
    private List<DataTableJsonObject> tableList;
}

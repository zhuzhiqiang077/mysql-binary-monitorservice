package com.yy.fastcustom.mysql.binarylogservice.dto;

import com.yy.fastcustom.mysql.binarylogservice.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zzq.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MysqlRowData {

    private String tableName;

    private String level;

    private OpType opType;

    private List<Map<String, String>> fieldValueMap = new ArrayList<>();
}

package com.yy.fastcustom.mysql.binarylogservice.listener;

import com.yy.fastcustom.mysql.binarylogservice.dto.BinaryLogRowData;
import com.yy.fastcustom.mysql.binarylogservice.dto.MysqlRowData;

/**
 * Created by zzq.
 */
public interface DataExportProcessor {
    void export0(BinaryLogRowData eventData);

    void export1(MysqlRowData eventData);
}

package com.yy.fastcustom.mysql.binarylogservice.listener;

import com.yy.fastcustom.mysql.binarylogservice.dto.BinlogRowData;
import com.yy.fastcustom.mysql.binarylogservice.dto.MysqlRowData;

/**
 * Created by zzq.
 */
public interface DataExportProcessor {
    void export0(BinlogRowData eventData);

    void export1(MysqlRowData eventData);
}

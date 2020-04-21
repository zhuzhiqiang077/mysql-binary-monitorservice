package com.yy.fastcustom.mysql.service;


import com.yy.fastcustom.mysql.binarylogservice.dto.MysqlRowData;

/**
 * Created by Qinyi.
 */
public interface ISender {

    void sender(MysqlRowData rowData);
}

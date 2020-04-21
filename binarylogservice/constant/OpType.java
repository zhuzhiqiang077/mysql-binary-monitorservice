package com.yy.fastcustom.mysql.binarylogservice.constant;

import com.github.shyiko.mysql.binlog.event.EventType;

/**
 * Created by zzq.
 */
public enum OpType {

    ADD,
    UPDATE,
    DELETE,
    OTHER;

    public static OpType to(EventType eventType) {

        switch (eventType) {
            case EXT_WRITE_ROWS:
            case WRITE_ROWS:
                return ADD;
            case EXT_UPDATE_ROWS:
            case UPDATE_ROWS:
                return UPDATE;
            case EXT_DELETE_ROWS:
            case DELETE_ROWS:
                return DELETE;
            default:
                return OTHER;
        }
    }
}

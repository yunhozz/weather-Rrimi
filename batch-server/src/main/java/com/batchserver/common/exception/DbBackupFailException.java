package com.batchserver.common.exception;

import com.batchserver.common.enums.ErrorCode;

public class DbBackupFailException extends AdminException {

    public DbBackupFailException() {
        super(ErrorCode.DB_BACKUP_FAIL);
    }
}
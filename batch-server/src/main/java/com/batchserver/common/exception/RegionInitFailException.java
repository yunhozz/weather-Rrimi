package com.batchserver.common.exception;

import com.batchserver.common.enums.ErrorCode;

public class RegionInitFailException extends AdminException {

    public RegionInitFailException() {
        super(ErrorCode.REGION_INIT_FAIL);
    }
}
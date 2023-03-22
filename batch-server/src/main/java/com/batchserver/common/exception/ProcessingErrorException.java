package com.batchserver.common.exception;

import com.batchserver.common.enums.ErrorCode;

public class ProcessingErrorException extends AdminException {

    public ProcessingErrorException() {
        super(ErrorCode.PROCESSING_ERROR);
    }
}
package com.zerobase.dividend.exception.impl;

import com.zerobase.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class ExistCompanyException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 회사입니다.";
    }
}

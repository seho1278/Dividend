package com.zerobase.dividend.exception;

// 에러 발생시 던져줄 모델 클래스

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}

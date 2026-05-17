package com.istp.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldErrorResponse {
    private String field;
    private String message;
}

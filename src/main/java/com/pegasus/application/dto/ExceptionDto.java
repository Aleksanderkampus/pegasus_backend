package com.pegasus.application.dto;

import com.pegasus.application.exeptions.AppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDto {

    private int code;
    private String message;

    public ExceptionDto(AppException e){
        setCode(e.getCode());
        setMessage(e.getMessage());
    }
}

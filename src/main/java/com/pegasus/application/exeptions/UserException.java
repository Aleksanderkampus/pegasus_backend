package com.pegasus.application.exeptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserException extends AppException{

    public UserException(Exception e){
        super(e);
    }

    public UserException(String message, int code){
        super(message,code);
    }

    public UserException(String message){
        super(message);
    }
}

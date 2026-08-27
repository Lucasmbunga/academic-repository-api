package com.lucas.academicrepository.util;

import java.util.List;

public class ResponseUtil {

    public static  <T> ApiResponse<T> sucess(T data,String message,String path){
        return new ApiResponse<>(true,data,null,0,message,System.currentTimeMillis(),path);
    }
    public static  <T> ApiResponse<T> error(List<String> errors,int errorCode, String message,String path){
        return new ApiResponse<>(false,null,errors,errorCode,message,System.currentTimeMillis(),path);
    }
}

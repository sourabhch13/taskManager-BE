package com.TaskManager.UtilityClasses;

public class ApiResponse<T> {
    private Integer StatusCode;
    private String Status;
    private String message;
    private T Data;

    public ApiResponse() {
    }

    public ApiResponse(Integer statusCode, String status, String message) {
        StatusCode = statusCode;
        Status = status;
        this.message = message;
    }

    public ApiResponse(Integer statusCode, String status, String message, T data) {
        StatusCode = statusCode;
        Status = status;
        this.message = message;
        Data = data;
    }

    public Integer getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(Integer statusCode) {
        StatusCode = statusCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}

package com.project.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileUploadException extends TimeFitException {

    private static final String MESSAGE = "파일 업로드에 실패했습니다.";

    public FileUploadException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
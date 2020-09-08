package com.dxy.library.network.http.exception;

import com.dxy.library.exception.FormativeException;

/**
 * @author duanxinyuan
 * 2020/6/24 15:52
 */
public class HttpException extends FormativeException {

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String format, Object... arguments) {
        super(format, arguments);
    }

}

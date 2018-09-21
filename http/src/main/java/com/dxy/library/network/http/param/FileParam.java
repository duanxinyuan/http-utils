package com.dxy.library.network.http.param;

import lombok.Data;

import java.io.File;
import java.io.InputStream;

/**
 * 文件参数
 * @author duanxinyuan
 * 2018/9/21 19:03
 */
@Data
public class FileParam {

    private String key;

    private File file;

    private InputStream inputStream;

    public FileParam(String key, File file) {
        this.key = key;
        this.file = file;
    }

    public FileParam(String key, InputStream inputStream) {
        this.key = key;
        this.inputStream = inputStream;
    }
}

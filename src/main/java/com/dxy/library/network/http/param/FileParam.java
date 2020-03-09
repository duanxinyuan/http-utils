package com.dxy.library.network.http.param;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;

/**
 * 文件参数
 * @author duanxinyuan
 * 2018/9/21 19:03
 */
@Data
public class FileParam {

    //文件表单数据名称
    private String name;

    //文件名称
    private String filename;

    //文件
    private File file;

    //流
    private InputStream inputStream;

    private FileParam() {
    }

    public String getFileName() {
        if (StringUtils.isNotEmpty(filename)) {
            return filename;
        } else {
            if (file != null) {
                return file.getName();
            } else {
                return name;
            }
        }
    }

    public FileParam(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public FileParam(String name, String filename, File file) {
        this.name = name;
        this.filename = filename;
        this.file = file;
    }

    public FileParam(String name, String filename, InputStream inputStream) {
        this.name = name;
        this.filename = filename;
        this.inputStream = inputStream;
    }
}

package com.dxy.library.network.http.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author duanxinyuan
 * 2019/1/14 18:45
 */
public class StreamUtils {

    public static InputStream cloneInputStream(final InputStream inputStream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readLength);
            }
            outputStream.flush();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

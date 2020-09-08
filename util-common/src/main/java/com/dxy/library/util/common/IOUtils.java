package com.dxy.library.util.common;


import com.google.common.collect.Lists;

import java.io.*;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author duanxinyuan
 * 2019/4/12 15:19
 */
public class IOUtils extends org.apache.commons.io.IOUtils {

    /**
     * 克隆输入流
     */
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将流至字符串List中
     * @return 文件内容字符串
     */
    public static List<String> readAsString(InputStream inputStream) {
        List<String> result = Lists.newArrayList();
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 单线程异步读取流内容
     * @param inputStream 输入流
     * @param readCallback 回调接口
     */
    public static void readAsync(InputStream inputStream, ReadCallback readCallback) {
        readAsync(inputStream, 1, readCallback);
    }

    /**
     * 多线程异步读取流内容
     * @param inputStream 输入流
     * @param threadCount 线程数
     * @param readCallback 回调接口
     */
    public static void readAsync(InputStream inputStream, int threadCount, ReadCallback readCallback) {
        if (threadCount < 1) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            ExecutorService executorService = ExecutorUtils.getExecutorService("ReadFileAsync" + Clock.systemUTC().millis(), threadCount);
            String content;
            while ((content = reader.readLine()) != null) {
                String finalContent = content;
                executorService.execute(() -> readCallback.read(finalContent));
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }


    public interface ReadCallback {
        /**
         * 读取文件
         */
        void read(String content);
    }

}

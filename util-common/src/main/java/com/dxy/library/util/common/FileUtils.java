package com.dxy.library.util.common;

import com.google.common.collect.Lists;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * @author duanxinyuan
 * 2018/5/2 20:32
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * 创建文件
     */
    public static File createFile(String path) {
        File localFile = new File(path);
        if (localFile.isDirectory()) {
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
        } else {
            String localPathDir = localFile.getPath().substring(0, localFile.getPath().lastIndexOf(File.separator));
            File localPathDirFile = new File(localPathDir);
            if (!localPathDirFile.exists()) {
                localPathDirFile.mkdirs();
            }
            if (!localFile.exists()) {
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new File(path);
    }

    /**
     * 创建文件夹
     */
    public static File createDirectory(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 删除文件或者文件夹
     * @param path 文件路径
     */
    public static void delete(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        } else if (file.exists() && file.isDirectory()) {
            String[] fileNames = file.list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    delete(path + File.separator + fileName);
                }
            }
            file.delete();
        }
    }

    /**
     * 判断文件名是否是zip文件
     * @param fileName 需要判断的文件路径
     * @return 是zip文件返回true, 否则返回false
     */
    public static boolean isZip(String fileName) {
        return StringUtils.isNotEmpty(fileName) && (fileName.endsWith(".ZIP") || fileName.endsWith(".zip"));
    }

    /**
     * 判断文件名是否是tar文件
     * @param path 需要判断的文件路径
     * @return 是tar文件返回true, 否则返回false
     */
    public static boolean isTar(String path) {
        return StringUtils.isNotEmpty(path) && (path.endsWith(".TAR.GZ") || path.endsWith(".tar.gz"));
    }

    /**
     * 获取文件名称
     * @param path 文件路径
     */
    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 递归取到文件夹下的所有文件
     */
    public static List<String> getFiles(String path) {
        return getFiles(new File(path));
    }

    /**
     * 递归取到文件夹下的所有文件
     */
    public static List<String> getFiles(File file) {
        if (file.isFile()) {
            return Lists.newArrayList(file.getAbsolutePath());
        } else {
            List<String> lstFiles = new ArrayList<>();
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        lstFiles.add(f.getAbsolutePath());
                        lstFiles.addAll(getFiles(f));
                    } else {
                        String str = f.getAbsolutePath();
                        lstFiles.add(str);
                    }
                }
            }
            return lstFiles;
        }
    }

    /**
     * 将流写入指定路径
     * @param inputStream 流
     * @param targetPath 目标文件
     */
    public static void writeInputStreamToFile(String targetPath, InputStream inputStream) {
        File file = createFile(targetPath);
        writeInputStreamToFile(file, inputStream);
    }

    /**
     * 将流写入指定路径
     * @param file 目标文件
     * @param inputStream 流
     */
    public static void writeInputStreamToFile(File file, InputStream inputStream) {
        try {
            writeByteArrayToFile(file, IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将文件读取至字符串List中
     * @param path 文件路径
     * @return 文件内容字符串
     */
    public static List<String> readLines(String path) {
        try {
            return readLines(new File(path), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 单线程异步读取流内容
     * @param path 文件路径
     * @param readCallback 回调接口
     */
    public static void readFileAsync(String path, IOUtils.ReadCallback readCallback) {
        try {
            IOUtils.readAsync(new FileInputStream(path), readCallback);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 多线程异步读取流内容
     * @param path 文件路径
     * @param threadCount 线程数
     * @param readCallback 回调接口
     */
    public static void readFileAsync(String path, int threadCount, IOUtils.ReadCallback readCallback) {
        try {
            IOUtils.readAsync(new FileInputStream(path), threadCount, readCallback);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件按照指定行数分割
     */
    public static DataIterator splitTextFile(String file, int pageSize) {
        if (StringUtils.isEmpty(file)) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new StringReader(file));
        return new DataIterator(reader, pageSize);
    }

    /**
     * 文件按照指定行数分割
     */
    public static DataIterator splitTextFile(Reader file, int pageSize) {
        if (file == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(file);
        return new DataIterator(reader, pageSize);
    }

    /**
     * 文件按照指定行分割
     */
    public static DataIterator splitTextFile(File file, int pageSize) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return splitTextFile(new InputStreamReader(new FileInputStream(file)), pageSize);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class DataIterator {
        private BufferedReader reader;
        private int pageSize;
        private String nextLine;

        public DataIterator(BufferedReader reader, int pageSize) {
            this.reader = reader;
            this.pageSize = pageSize;
        }

        public boolean hasNext() {
            try {
                nextLine = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            boolean flag = nextLine != null;
            if (!flag) {
                close();
            }
            return flag;
        }

        public byte[] next() {
            try {
                StringWriter writer = new StringWriter();
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                int i = 0;
                while (i < pageSize && (nextLine != null || hasNext())) {
                    i++;
                    bufferedWriter.write(nextLine);
                    nextLine = null;
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
                return writer.toString().getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

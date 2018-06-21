package com.mob.network.okhttp.builder;


import com.dxy.library.json.GsonUtil;
import com.mob.network.okhttp.constant.Method;
import com.mob.network.okhttp.header.Headers;
import com.mob.network.okhttp.param.Params;
import okhttp3.*;
import okio.ByteString;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * 请求构建者基类
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class OkBuilder extends Request.Builder {

    public static <T> OkBuilder builder(Method method, String url, com.mob.network.okhttp.header.Headers headers, Params params, T t, String fileKey, File file, String[] fileKeys, File[] files) {
        switch (method) {
            case GET:
                //GET不支持传输Body
                return GetBuilder.getBuilder().buildGet(url, headers, params);
            case POST:
                if (null == t && null == fileKey & null == fileKeys) {
                    return PostBuilder.getBuilder().buildPost(url, headers, params);
                } else {
                    if (null != t) {
                        return PostBuilder.getBuilder().buildPost(url, headers, t, MediaType.parse("application/json; charset=utf-8"));
                    }
                    if (null != fileKey) {
                        return PostBuilder.getBuilder().buildPost(url, fileKey, file, params);
                    }
                    return PostBuilder.getBuilder().buildPost(url, fileKeys, files, params);
                }
            case PUT:
                if (null == t) {
                    return PutBuilder.getBuilder().buildPut(url, headers, params);
                } else {
                    return PutBuilder.getBuilder().buildPut(url, headers, t, MediaType.parse("application/json; charset=utf-8"));
                }
            case PATCH:
                if (null == t) {
                    return PatchBuilder.getBuilder().buildPatch(url, headers, params);
                } else {
                    return PatchBuilder.getBuilder().buildPatch(url, headers, t, MediaType.parse("application/json; charset=utf-8"));
                }
            case DELETE:
                //DELETE不支持传输Body
                return DeleteBuilder.getBuilder().buildDelete(url, headers, params);
            default:
                return null;
        }
    }

    <T> RequestBody getRequestBody(com.mob.network.okhttp.header.Headers headers, T t, MediaType type) {
        addHeader(headers);
        if (t instanceof String) {
            return RequestBody.create(type, (String) t);
        } else if (t instanceof byte[]) {
            return RequestBody.create(type, (byte[]) t);
        } else if (t instanceof ByteString) {
            return RequestBody.create(type, (ByteString) t);
        } else {
            return RequestBody.create(type, GsonUtil.to(t));
        }
    }

    RequestBody getRequestBody(com.mob.network.okhttp.header.Headers headers) {
        addHeader(headers);
        return new FormBody.Builder().build();
    }

    RequestBody getRequestBody(Params params) {
        FormBody.Builder builder = new FormBody.Builder();
        addFormData(builder, params);
        return builder.build();
    }

    RequestBody getRequestBody(com.mob.network.okhttp.header.Headers headers, Params params) {
        addHeader(headers);
        FormBody.Builder builder = new FormBody.Builder();
        addFormData(builder, params);
        return builder.build();
    }

    RequestBody getRequestBody(File[] files, String[] fileKeys, Params params) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addFormDataPart(builder, params);
        if (files != null) {
            RequestBody fileBody;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addFormDataPart(fileKeys[i], fileName, fileBody);
            }
        }
        return builder.build();
    }

    RequestBody getRequestBody(File file, String fileKey, Params params) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addFormDataPart(builder, params);
        if (null == file || null == fileKey) {
            return null;
        }
        String fileName = file.getName();
        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
        builder.addFormDataPart(fileKey, fileName, fileBody);
        return builder.build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 添加Header
     */
    void addHeader(Headers headers) {
        if (null != headers && headers.size() > 0) {
            headers.forEach((k, v) -> {
                if (null != k && null != v) {
                    addHeader(k, v);
                }
            });
        }
    }

    private void addFormData(FormBody.Builder builder, Params params) {
        if (null != params && params.size() > 0) {
            params.forEach((k, v) -> {
                if (null != k && null != v) {
                    builder.add(k, v);
                }
            });
        }
    }

    private void addFormDataPart(MultipartBody.Builder builder, Params params) {
        if (null != params && params.size() > 0) {
            params.forEach((k, v) -> {
                if (null != k && null != v) {
                    builder.addFormDataPart(k, v);
                }
            });
        }
    }

    void addQueryParameter(HttpUrl.Builder builder, Params params) {
        if (null != params && params.size() > 0) {
            params.forEach((k, v) -> {
                if (null != k && null != v) {
                    builder.addQueryParameter(k, v);
                }
            });
        }
    }

}

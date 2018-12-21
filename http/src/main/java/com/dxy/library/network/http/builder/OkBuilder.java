package com.dxy.library.network.http.builder;


import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.network.http.constant.Method;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * 请求构建者基类
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class OkBuilder extends Request.Builder {
    private static final MediaType MEDIA_TYPE_APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/*; charset=utf-8");
    private static final MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream; charset=utf-8");

    public static <T> OkBuilder builder(Method method, String url, Headers headers, Params params, T t, List<FileParam> fileParams) {
        switch (method) {
            case GET:
                //GET不支持传输Body
                return GetBuilder.getBuilder().buildGet(url, headers, params);
            case POST:
                if (null != fileParams && fileParams.size() > 0) {
                    return PostBuilder.getBuilder().buildPost(url, headers, params, fileParams);
                } else {
                    if (null == t) {
                        return PostBuilder.getBuilder().buildPost(url, headers, params);
                    } else {
                        return PostBuilder.getBuilder().buildPost(url, headers, params, t, getMediaType(headers));
                    }
                }
            case PUT:
                if (null == t) {
                    return PutBuilder.getBuilder().buildPut(url, headers, params);
                } else {
                    return PutBuilder.getBuilder().buildPut(url, headers, params, t, getMediaType(headers));
                }
            case PATCH:
                if (null == t) {
                    return PatchBuilder.getBuilder().buildPatch(url, headers, params);
                } else {
                    return PatchBuilder.getBuilder().buildPatch(url, headers, params, t, getMediaType(headers));
                }
            case DELETE:
                //DELETE不支持传输Body
                return DeleteBuilder.getBuilder().buildDelete(url, headers, params);
            default:
                return null;
        }
    }

    private static MediaType getMediaType(Headers headers) {
        if (headers == null || !headers.containsContentType()) {
            return MEDIA_TYPE_APPLICATION_JSON;
        } else {
            return MediaType.parse(headers.getContentType());
        }
    }

    <T> RequestBody getRequestBody(Headers headers, T t, MediaType type) {
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

    RequestBody getRequestBody(Headers headers, Params params) {
        addHeader(headers);
        FormBody.Builder builder = new FormBody.Builder();
        addFormData(builder, params);
        return builder.build();
    }

    RequestBody getRequestBody(Headers headers, Params params, List<FileParam> fileParams) {
        addHeader(headers);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addFormDataPart(builder, params);
        if (fileParams != null && fileParams.size() > 0) {
            for (FileParam fileParam : fileParams) {
                addFormDataPart(builder, fileParam);
            }
        }
        return builder.build();
    }

    private void addFormDataPart(MultipartBody.Builder builder, FileParam fileParam) {
        if (null == fileParam) {
            return;
        }
        if (fileParam.getFile() != null) {
            RequestBody fileBody = RequestBody.create(guessMimeType(fileParam.getFileName()), fileParam.getFile());
            builder.addFormDataPart(fileParam.getName(), getHeaderValue(fileParam.getFileName()), fileBody);
        } else {
            RequestBody fileBody = getRequestBody(fileParam.getInputStream());
            builder.addFormDataPart(fileParam.getName(), getHeaderValue(fileParam.getFileName()), fileBody);
        }
    }

    private MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MEDIA_TYPE_OCTET_STREAM;
        } else {
            return MediaType.parse(contentType);
        }
    }

    private RequestBody getRequestBody(InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_TEXT;
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }

    /**
     * 添加Header
     */
    void addHeader(Headers headers) {
        if (null != headers && headers.size() > 0) {
            headers.forEach((key, value) -> {
                if (null != key && null != value) {
                    super.addHeader(getHeaderKey(key), getHeaderValue(value));
                }
            });
        }
    }

    /**
     * OkHttp的Header中的value不支持 null、\n和中文等特殊字符
     * 所以替换 \n ，再使用OkHttp的校验方式，校验不通过的话，就返回URL编码后的字符串
     */
    private static String getHeaderValue(String value) {
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if ((c <= '\u001f' && c != '\t') || c >= '\u007f') {
                try {
                    return URLEncoder.encode(newValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return newValue;
                }
            }
        }
        return newValue;
    }

    /**
     * OkHttp的Header中的key不支持 null、\n和中文等特殊字符
     * 所以替换 \n ，再使用OkHttp的校验方式，校验不通过的话，就返回URL编码后的字符串
     */
    private static String getHeaderKey(String value) {
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u0020' || c >= '\u007f') {
                try {
                    return URLEncoder.encode(newValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return newValue;
                }
            }
        }
        return newValue;
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
            params.forEach((key, value) -> {
                if (null != key && null != value) {
                    builder.addFormDataPart(key, getHeaderValue(value));
                }
            });
        }
    }

    HttpUrl addQueryParameter(String url, Params params) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (null != httpUrl && null != params && params.size() > 0) {
            HttpUrl.Builder builder = httpUrl.newBuilder();
            params.forEach((k, v) -> {
                if (null != k && null != v) {
                    builder.addQueryParameter(k, v);
                }
            });
            return builder.build();
        } else {
            return httpUrl;
        }
    }

}

package cn.gduf.xytg.user.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 请求微信工具类
 * @date 2025/10/25 15:32
 */
public class HttpClientUtils {

    /** 连接超时时间（毫秒） */
    public static final int connTimeout=10000;

    /** 读取响应超时时间（毫秒） */
    public static final int readTimeout=10000;

    /** 默认字符编码 */
    public static final String charset="UTF-8";

    /** 全局共享的HttpClient实例 */
    private static HttpClient client = null;

    /**
     * 初始化连接池管理器并创建全局HttpClient实例
     * 设置最大连接数和每个路由的最大连接数均为128
     */
    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 发送POST请求，使用默认配置参数
     *
     * @param url 目标URL地址
     * @param parameterStr 请求体字符串参数
     * @return 响应结果字符串
     * @throws ConnectTimeoutException 连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 其他异常情况
     */
    public static String postParameters(String url, String parameterStr) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }

    /**
     * 发送POST请求，可自定义字符集与超时设置
     *
     * @param url 目标URL地址
     * @param parameterStr 请求体字符串参数
     * @param charset 字符编码格式
     * @param connTimeout 连接超时时间（毫秒）
     * @param readTimeout 读取响应超时时间（毫秒）
     * @return 响应结果字符串
     * @throws ConnectTimeoutException 连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 其他异常情况
     */
    public static String postParameters(String url, String parameterStr,String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }

    /**
     * 发送POST表单请求，默认使用UTF-8编码及默认超时时间
     *
     * @param url 目标URL地址
     * @param params 表单键值对参数集合
     * @return 响应结果字符串
     * @throws ConnectTimeoutException 连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 其他异常情况
     */
    public static String postParameters(String url, Map<String, String> params) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }

    /**
     * 发送POST表单请求，支持自定义超时时间
     *
     * @param url 目标URL地址
     * @param params 表单键值对参数集合
     * @param connTimeout 连接超时时间（毫秒）
     * @param readTimeout 读取响应超时时间（毫秒）
     * @return 响应结果字符串
     * @throws ConnectTimeoutException 连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 其他异常情况
     */
    public static String postParameters(String url, Map<String, String> params, Integer connTimeout,Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }

    /**
     * 发送GET请求，使用默认字符集和超时时间
     *
     * @param url 目标URL地址
     * @return 响应结果字符串
     * @throws Exception 异常信息
     */
    public static String get(String url) throws Exception {
        return get(url, charset, null, null);
    }

    /**
     * 发送GET请求，指定字符集但使用默认超时时间
     *
     * @param url 目标URL地址
     * @param charset 字符编码格式
     * @return 响应结果字符串
     * @throws Exception 异常信息
     */
    public static String get(String url, String charset) throws Exception {
        return get(url, charset, connTimeout, readTimeout);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url 目标URL地址
     * @param body RequestBody请求体内容
     * @param mimeType MIME类型，如 application/xml 或 application/x-www-form-urlencoded 等
     * @param charset 编码方式
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException  响应超时
     * @throws Exception 其它异常
     */
    public static String post(String url, String body, String mimeType,String charset, Integer connTimeout, Integer readTimeout)
            throws ConnectTimeoutException, SocketTimeoutException, Exception {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置请求配置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null&& client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }

    /**
     * 提交form表单数据到目标地址
     *
     * @param url 目标URL地址
     * @param params 表单参数键值对映射
     * @param headers 自定义HTTP头字段
     * @param connTimeout 连接超时时间（毫秒）
     * @param readTimeout 响应读取超时时间（毫秒）
     * @return 响应结果字符串
     * @throws ConnectTimeoutException 连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 其它异常
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout,Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                post.setEntity(entity);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置请求配置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse res = null;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url 目标URL地址
     * @param charset 字符编码格式
     * @param connTimeout  建立链接超时时间,毫秒.
     * @param readTimeout  响应超时时间,毫秒.
     * @return 响应结果字符串
     * @throws ConnectTimeoutException   建立链接超时
     * @throws SocketTimeoutException   响应超时
     * @throws Exception 其它异常
     */
    public static String get(String url, String charset, Integer connTimeout,Integer readTimeout)
            throws ConnectTimeoutException,SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpGet get = new HttpGet(url);
        String result = "";
        try {
            // 设置请求配置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());

            HttpResponse res = null;

            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(get);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            get.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }

    /**
     * 从 HTTP 响应中提取字符集信息
     *
     * @param ressponse HTTP响应对象
     * @return 返回Content-Type中的字符集名称或null
     */
    @SuppressWarnings("unused")
    private static String getCharsetFromResponse(HttpResponse ressponse) {
        // Content-Type:text/html; charset=GBK
        if (ressponse.getEntity() != null  && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null) {
            String contentType = ressponse.getEntity().getContentType().getValue();
            if (contentType.contains("charset=")) {
                return contentType.substring(contentType.indexOf("charset=") + 8);
            }
        }
        return null;
    }

    /**
     * 创建一个忽略SSL证书验证的安全HTTPS客户端
     *
     * @return CloseableHttpClient 实例
     * @throws GeneralSecurityException 安全相关异常
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
                @Override
                public void verify(String host, SSLSocket ssl)
                        throws IOException {
                }
                @Override
                public void verify(String host, X509Certificate cert)
                        throws SSLException {
                }
                @Override
                public void verify(String host, String[] cns,
                                   String[] subjectAlts) throws SSLException {
                }
            });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }
}

package cn.gduf.xytg.payment.utils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 支付服务接口
 * @date 2025/10/30 20:30
 */
public class HttpClient {

    private String url;
    private Map<String, String> param;
    private int statusCode;
    private String content;
    private String xmlParam;
    private boolean isHttps;
    private boolean isCert = false;
    //证书密码 微信商户号（mch_id）
    private String certPassword;

    /**
     * 判断是否使用HTTPS协议
     *
     * @return true表示使用HTTPS，false表示不使用
     */
    public boolean isHttps() {
        return isHttps;
    }

    /**
     * 设置是否使用HTTPS协议
     *
     * @param isHttps true表示使用HTTPS，false表示不使用
     */
    public void setHttps(boolean isHttps) {
        this.isHttps = isHttps;
    }

    /**
     * 判断是否需要证书认证
     *
     * @return true表示需要证书认证，false表示不需要
     */
    public boolean isCert() {
        return isCert;
    }

    /**
     * 设置是否需要证书认证
     *
     * @param cert true表示需要证书认证，false表示不需要
     */
    public void setCert(boolean cert) {
        isCert = cert;
    }

    /**
     * 获取XML格式的请求参数
     *
     * @return XML格式的请求参数字符串
     */
    public String getXmlParam() {
        return xmlParam;
    }

    /**
     * 设置XML格式的请求参数
     *
     * @param xmlParam XML格式的请求参数字符串
     */
    public void setXmlParam(String xmlParam) {
        this.xmlParam = xmlParam;
    }

    /**
     * 构造函数，初始化URL和参数Map
     *
     * @param url   请求地址
     * @param param 请求参数Map
     */
    public HttpClient(String url, Map<String, String> param) {
        this.url = url;
        this.param = param;
    }

    /**
     * 构造函数，仅初始化URL
     *
     * @param url 请求地址
     */
    public HttpClient(String url) {
        this.url = url;
    }

    /**
     * 获取证书密码
     *
     * @return 证书密码字符串
     */
    public String getCertPassword() {
        return certPassword;
    }

    /**
     * 设置证书密码
     *
     * @param certPassword 证书密码字符串
     */
    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    /**
     * 设置请求参数Map
     *
     * @param map 请求参数Map
     */
    public void setParameter(Map<String, String> map) {
        param = map;
    }

    /**
     * 添加单个请求参数
     *
     * @param key   参数名
     * @param value 参数值
     */
    public void addParameter(String key, String value) {
        if (param == null)
            param = new HashMap<String, String>();
        param.put(key, value);
    }

    /**
     * 发送POST请求
     *
     * @throws ClientProtocolException HTTP协议异常
     * @throws IOException             IO异常
     */
    public void post() throws ClientProtocolException, IOException {
        HttpPost http = new HttpPost(url);
        setEntity(http);
        execute(http);
    }

    /**
     * 发送PUT请求
     *
     * @throws ClientProtocolException HTTP协议异常
     * @throws IOException             IO异常
     */
    public void put() throws ClientProtocolException, IOException {
        HttpPut http = new HttpPut(url);
        setEntity(http);
        execute(http);
    }

    /**
     * 发送GET请求
     *
     * @throws ClientProtocolException HTTP协议异常
     * @throws IOException             IO异常
     */
    public void get() throws ClientProtocolException, IOException {
        // 构造GET请求的URL参数
        if (param != null) {
            StringBuilder url = new StringBuilder(this.url);
            boolean isFirst = true;
            for (String key : param.keySet()) {
                if (isFirst)
                    url.append("?");
                else
                    url.append("&");
                url.append(key).append("=").append(param.get(key));
            }
            this.url = url.toString();
        }
        HttpGet http = new HttpGet(url);
        execute(http);
    }

    /**
     * 设置HTTP请求实体，支持表单参数和XML参数
     *
     * @param http HTTP请求对象
     */
    private void setEntity(HttpEntityEnclosingRequestBase http) {
        if (param != null) {
            List<NameValuePair> nvps = new LinkedList<NameValuePair>();
            for (String key : param.keySet())
                nvps.add(new BasicNameValuePair(key, param.get(key))); // 参数
            http.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8)); // 设置参数
        }
        if (xmlParam != null) {
            http.setEntity(new StringEntity(xmlParam, Consts.UTF_8));
        }
    }

    /**
     * 执行HTTP请求并处理响应
     *
     * @param http HTTP请求对象
     * @throws ClientProtocolException HTTP协议异常
     * @throws IOException             IO异常
     */
    private void execute(HttpUriRequest http) throws ClientProtocolException,
            IOException {
        CloseableHttpClient httpClient = null;
        try {
            // 根据是否使用HTTPS和证书认证创建不同的HTTP客户端
            if (isHttps) {
                if (isCert) {
                    // 加载证书并创建带证书认证的HTTPS客户端
                    FileInputStream inputStream = new FileInputStream(new File(ConstantPropertiesUtils.CERT));
                    KeyStore keystore = KeyStore.getInstance("PKCS12");
                    char[] partnerId2charArray = certPassword.toCharArray();
                    keystore.load(inputStream, partnerId2charArray);
                    SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
                    SSLConnectionSocketFactory sslsf =
                            new SSLConnectionSocketFactory(sslContext,
                                    new String[]{"TLSv1"},
                                    null,
                                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                } else {
                    // 创建信任所有证书的HTTPS客户端
                    SSLContext sslContext = new SSLContextBuilder()
                            .loadTrustMaterial(null, new TrustStrategy() {
                                // 信任所有证书
                                public boolean isTrusted(X509Certificate[] chain,
                                                         String authType)
                                        throws CertificateException {
                                    return true;
                                }
                            }).build();
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                            sslContext);
                    httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                            .build();
                }
            } else {
                // 创建普通HTTP客户端
                httpClient = HttpClients.createDefault();
            }
            CloseableHttpResponse response = httpClient.execute(http);
            try {
                if (response != null) {
                    if (response.getStatusLine() != null)
                        statusCode = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();
                    // 响应内容
                    content = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
    }

    /**
     * 获取HTTP响应状态码
     *
     * @return HTTP状态码
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 获取HTTP响应内容
     *
     * @return 响应内容字符串
     * @throws ParseException 解析异常
     * @throws IOException    IO异常
     */
    public String getContent() throws ParseException, IOException {
        return content;
    }
}

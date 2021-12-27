package com.alidaodao.web.tools;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * http request util
 * </p>
 *
 * @author songbo
 * @date 2021-12-27 13:34
 * @since
 */
public class HttpUtil {

    /**
     * log
     */
    private static final Logger logger = Logger.getLogger(HttpUtil.class.getName());

    /** 全局HTTP-Client */
    private static CloseableHttpClient httpClient;

    /**
     * 获取Http客户端连接对象
     *
     * @return Http客户端连接对象
     */
    public synchronized static CloseableHttpClient getHttpClient(int timeout,int maxIdleTime) {
        if (httpClient == null) {
            // 创建httpClient
            httpClient = HttpClients.custom()
                    // 把请求相关的超时信息设置到连接客户端
                    .setDefaultRequestConfig(initRequestConfig(timeout))
                    // 把请求重试设置到连接客户端
                    .setRetryHandler(initHttpRequestRetryHandler())
                    // 配置连接池管理对象
                    .setConnectionManager(initManager())
                    //开启过期关闭策略
                    .evictExpiredConnections()
                    //超过多长时间没有使用就关闭它
                    .evictIdleConnections(maxIdleTime, TimeUnit.SECONDS)
                    .build();
        }
        return httpClient;
    }

    /**
     * 测出超时重试机制为了防止超时不生效而设置
     *
     * @return
     */
    private static HttpRequestRetryHandler initHttpRequestRetryHandler() {
        /**
         * 测出超时重试机制为了防止超时不生效而设置
         *  如果直接放回false,不重试
         *  这里会根据情况进行判断是否重试
         */
        return (exception, executionCount, context) -> {
            if (executionCount >= 1) {// 如果已经重试了3次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return true;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {// ssl握手异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };
    }

    /**
     * 请求时间配置
     *
     * @param timeOut
     * @return
     */
    private static RequestConfig initRequestConfig(int timeOut) {
        return RequestConfig.custom()
                // 获取连接超时时间
                .setConnectionRequestTimeout(timeOut)
                // 请求超时时间
                .setConnectTimeout(timeOut)
                // 响应超时时间
                .setSocketTimeout(timeOut)
                .build();
    }

    /**
     * 连接池配置
     *
     * @return
     */
    private static PoolingHttpClientConnectionManager initManager() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connManager.setMaxTotal(200);
        // 设置每个连接的路由数
        connManager.setDefaultMaxPerRoute(20);
        return connManager;
    }


    /**
     * 简单post请求
     *
     * @param url
     * @param body
     * @return
     */
    public static String simpleHttpPost(String url, String body) {
        return httpPost(url, body, 3000, 3600);
    }
    /**
     * 发送http post请求
     *
     * @param url url
     * @param body body
     * @param timeout 连接超时或者请求超时时间
     * @param maxIdleTime http自动关闭时间
     * @return
     * @throws
     */
    public static String httpPost(String url, String body, int timeout, int maxIdleTime) {
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = getHttpClient(timeout, maxIdleTime)) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            //如果返回值ok,那么返回对应的对象
            if (HttpStatus.SC_OK <= status
                    && HttpStatus.SC_MULTIPLE_CHOICES > status) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("[httpPost][postRequest][error]url: %s,body: %s", url, body), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.format("[httpPost][closeResponse][error]url: %s,body: %s", url, body), e);
                }
            }
        }
        return null;
    }
}

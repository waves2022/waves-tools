package com.alidaodao.web.message;

/**
 * <p>
 * 发送消息方法
 * </p>
 *
 * @author songbo
 * @date 2021-12-24 16:49
 * @since
 */
public  abstract class MessageAbstractService {

    /**
     * sendMessage
     *
     * @throws Exception
     */
    public abstract void sendTo(String url, String token, String message);

    /**
     * send Message
     *
     * @param token
     * @param message
     */
    public abstract void sendTo(String token, String message);

    /**
     * init service, invoked when Message init
     */
    public void init() throws Exception {
        // do something
    }


    /**
     * destroy message, invoked when Message destroy
     */
    public void destroy() throws Exception {
        // do something
    }

}

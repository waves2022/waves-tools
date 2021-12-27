package com.alidaodao.web.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alidaodao.web.commom.Constants;
import com.alidaodao.web.tools.HttpUtil;
import java.util.Objects;

/**
 * <p>
 * 飞书发送消息实现
 * </p>
 *
 * @author songbo
 * @date 2021-12-24 16:53
 * @since
 */
public class FeishuMessageHelper {


    /**
     * simple send message
     *
     * @param token
     * @param message
     * @return
     */

    public static boolean simpleSendTo(String token, String message) {
        return sendTo(Constants.FEISHU_ROBOT_URL, token, message);
    }

    /**
     * sendMessage
     *
     * @param url
     * @param token
     * @param message
     * @throws Exception
     */
    public static boolean sendTo(String url, String token, String message) {
        JSONObject body = new JSONObject();
        body.put("msg_type", "text");
        JSONObject dataJson = new JSONObject();
        dataJson.put("text", message);
        body.put("content", dataJson);
        //发送消息
        String response = HttpUtil.httpPost(url + token, JSON.toJSONString(body), 3000, 3600);
        return Objects.nonNull(response);
    }
}

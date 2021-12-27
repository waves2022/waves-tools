package com.alidaodao.web.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alidaodao.web.commom.Constants;
import com.alidaodao.web.tools.HttpUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * work wx helper
 * </p>
 *
 * @author songbo
 * @date 2021-12-27 15:32
 * @since
 */
public class WorkWxMessageHelper {


    /**
     * simple send message
     *
     * @param token
     * @param message
     * @return
     */

    public static boolean simpleSendTo(String token, String message) {
        return sendTo(Constants.WORK_WX_ROBOT_URL, token, message);
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
        if (StringUtils.isAnyBlank(token, message)) {
            return false;
        }
        JSONObject text = new JSONObject();
        text.put("content", message);
        JSONObject msg = new JSONObject();
        msg.put("msgtype", "markdown");
        msg.put("markdown", text);
        //发送消息
        String response = HttpUtil.httpPost(url + token, JSON.toJSONString(msg), 3000, 3600);
        return Objects.nonNull(response);
    }

    /**
     * @param url
     * @param token
     * @param message
     * @return
     */
    public static boolean sendTextTo(String url, String token, String message, List<String> users) {
        if (StringUtils.isAnyBlank(token, message) || Objects.isNull(users) || users.size() <= 0) {
            return false;
        }
        JSONObject text = new JSONObject();
        text.put("content", message);
        text.put("mentioned_list", users);
        JSONObject msg = new JSONObject();
        msg.put("msgtype", "text");
        msg.put("text", text);
        //发送消息
        String response = HttpUtil.httpPost(url + token, JSON.toJSONString(msg), 3000, 3600);
        return Objects.nonNull(response);
    }

    /**
     * @param token
     * @param title
     * @param desc
     * @param nextUrl
     * @param picUrl
     * @return
     */
    public static boolean sendArticleTo(String token, String title, String desc, String nextUrl, String picUrl) {
        if (StringUtils.isAnyBlank(token, title, desc, nextUrl, picUrl)) {
            return false;
        }
        JSONObject content = new JSONObject();
        content.put("title", title);
        content.put("description", desc);
        content.put("url", nextUrl);
        content.put("picurl", picUrl);
        JSONObject text = new JSONObject();
        text.put("articles", Lists.newArrayList(content));
        JSONObject msg = new JSONObject();
        msg.put("msgtype", "news");
        msg.put("news", text);
        //发送消息
        String response = HttpUtil.httpPost(Constants.WORK_WX_ROBOT_URL + token, JSON.toJSONString(msg), 3000, 3600);
        return Objects.nonNull(response);
    }
}

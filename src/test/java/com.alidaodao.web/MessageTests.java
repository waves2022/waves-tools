package com.alidaodao.web;

import com.alidaodao.web.commom.Constants;
import com.alidaodao.web.message.FeishuMessageHelper;
import com.alidaodao.web.message.WorkWxMessageHelper;

/**
 * <p>
 * messageTests
 * </p>
 *
 * @author songbo
 * @date 2021-12-27 14:55
 * @since
 */
public class MessageTests {

    public static void main(String[] args) {
//        FeishuMessageHelper.sendTo(Constants.FEISHU_ROBOT_URL,"xxxxx","test");
        WorkWxMessageHelper.sendTo(Constants.WORK_WX_ROBOT_URL,"xxxx","sned work wx message");
    }



}

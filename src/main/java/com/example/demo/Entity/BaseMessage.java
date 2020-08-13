package com.example.demo.Entity;

import java.util.Map;

public class BaseMessage {
    private String ToUserName;
    private String FromUserName;
    private String CreateTime;
    private String MsgType;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public BaseMessage(Map<String, String> requestMap) {
        this.setToUserName(requestMap.get("FromUserName"));
        this.setFromUserName(requestMap.get("ToUserName"));
        this.setCreateTime(System.currentTimeMillis() / 1000 + "");
    }
}

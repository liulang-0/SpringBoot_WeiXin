package com.example.demo.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

@XStreamAlias("xml")
public class TextMessage extends BaseMessage {
    private String Content;


    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }


    public TextMessage(Map<String, String> requestMap, String content) {
        super(requestMap);
        //设置消息类型
        this.setMsgType("text");
        this.setContent(content);
    }
}

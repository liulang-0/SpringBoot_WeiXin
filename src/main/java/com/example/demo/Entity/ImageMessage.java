package com.example.demo.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

@XStreamAlias("xml")
public class ImageMessage extends BaseMessage  {
    private Image Image;

    public com.example.demo.Entity.Image getImage() {
        return Image;
    }

    public void setImage(com.example.demo.Entity.Image image) {
        Image = image;
    }

    public ImageMessage(Map<String, String> requestMap, Image image) {
        super(requestMap);
        //设置消息类型
        this.setMsgType("image");
        this.setImage(image);
    }
}

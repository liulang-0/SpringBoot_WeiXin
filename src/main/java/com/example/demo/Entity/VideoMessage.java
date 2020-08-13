package com.example.demo.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

@XStreamAlias("xml")
public class VideoMessage extends BaseMessage  {
    private Video Video;

    public com.example.demo.Entity.Video getVideo() {
        return Video;
    }

    public void setVideo(com.example.demo.Entity.Video video) {
        Video = video;
    }

    public VideoMessage(Map<String, String> requestMap, Video video) {
        super(requestMap);
        //设置消息类型
        this.setMsgType("video");
        this.setVideo(video);
    }
}

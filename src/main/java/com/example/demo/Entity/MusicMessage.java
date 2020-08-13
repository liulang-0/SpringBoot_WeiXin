package com.example.demo.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;
@XStreamAlias("xml")
public class MusicMessage extends BaseMessage  {
    private Music Music;

    public com.example.demo.Entity.Music getMusic() {
        return Music;
    }

    public void setMusic(com.example.demo.Entity.Music music) {
        Music = music;
    }
    public MusicMessage(Map<String, String> requestMap, Music Music) {
        super(requestMap);
        //设置消息类型
        this.setMsgType("music");
        this.setMusic(Music);
    }
}

package com.example.demo.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Map;

@XStreamAlias("xml")
public class VoiceMessage extends BaseMessage  {
    private Voice Voice;

    public com.example.demo.Entity.Voice getVoice() {
        return Voice;
    }

    public void setVoice(com.example.demo.Entity.Voice voice) {
        Voice = voice;
    }

    public VoiceMessage(Map<String, String> requestMap, Voice voice) {
        super(requestMap);
        //设置消息类型
        this.setMsgType("voice");
        this.setVoice(voice);
    }
}

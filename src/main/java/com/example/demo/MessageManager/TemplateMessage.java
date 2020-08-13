package com.example.demo.MessageManager;


import com.example.demo.WxService;
import com.example.demo.util.Util;

public class TemplateMessage {
    /**
     * 设置行业
     */
    public void set() {
        String token = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String data = "{\n" +
                "    \"industry_id1\":\"1\",\n" +
                "    \"industry_id2\":\"2\"\n" +
                "}";
        String result = Util.post(url, data);
    }

    /**
     * 获取行业
     */
    public void get() {
        String token = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String result = Util.get(url);
    }
}

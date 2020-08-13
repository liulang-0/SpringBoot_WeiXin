package com.example.demo;

import com.baidu.aip.ocr.AipOcr;
import com.example.demo.Entity.*;
import com.example.demo.util.Util;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestWx {
    //设置APPID/AK/SK
    public static final String APP_ID = "21945987";//你的 App ID
    public static final String API_KEY = "nSvkLPm8mQ30h6UWYQPZNHMK";//你的 Api Key
    public static final String SECRET_KEY = "ffCljnV55DG4zbHE1PU6N1aWBwHdfMS1";//你的 Secret Key

    /**
     * 获取用户信息
     */
    @Test
    public void testUserInfo() {
        String openId = "opJXg6Nh64f8SEeLJQ1eW45aQNWw";
        String ticket = WxService.getUserInfo(openId);
        System.out.println(ticket);
    }

    /**
     * 获取临时二维码的Ticket
     */
    @Test
    public void testQrCodeTicket() {
        String ticket = WxService.getQrCodeTicket();
        System.out.println(ticket);
    }

    /**
     * 测试新增临时素材
     */
    @Test
    public void testuploadPostWithFile() {
        String url = "E:/1.jpg";
        String result = WxService.uploadPostWithFile(url, "image");
        System.out.println(result);
    }

    /**
     * 图片文字识别
     */
    @Test
    public void testAipOcr() {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用接口
        String path = "test.jpg";
        org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));
    }

    /**
     * 发送模板消息
     */

    @Test
    public void sendTemplateMessage() {
        String token = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String data = "{\n" +
                "           \"touser\":\"opJXg6Nh64f8SEeLJQ1eW45aQNWw\",\n" +
                "           \"template_id\":\"fG2gkhAQAFmSyzZOVi6WVVYEI_s3iOJ1HShGeJQaq6w\",\n" +
                "           \"data\":{\n" +
                "                   \"first\": {\n" +
                "                       \"value\":\"面试反馈！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"company\":{\n" +
                "                       \"value\":\"巧克力\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"time\": {\n" +
                "                       \"value\":\"2014年9月22日\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"result\": {\n" +
                "                       \"value\":\"2014年9月22日\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"remark\":{\n" +
                "                       \"value\":\"欢迎再次购买！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   }\n" +
                "           }\n" +
                "       }";
        String result = Util.post(url, data);
    }

    /**
     * 设置行业
     */
    @Test
    public void set() {
        String token = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String data = "{\n" +
                "    \"industry_id1\":\"1\",\n" +
                "    \"industry_id2\":\"2\"\n" +
                "}";
        String result = Util.post(url, data);
        System.out.println(result);
    }

    /**
     * 获取行业
     */
    @Test
    public void get() {
        String token = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String result = Util.get(url);
        System.out.println(result);
    }

    /**
     * 自定义菜单
     */
    @Test
    public void testButton() {
        //菜单对象
        Button btn = new Button();
        //第一个一级菜单
        btn.getButton().add(new ClickButton("一级点击", "1"));
        //第二个一级菜单
        btn.getButton().add(new ViewButton("一级跳转", "http://www.baidu.com"));
        //创建第三个一级菜单
        SubButton sb = new SubButton("有子菜单");
        //第三个一级菜单增加子菜单
        sb.getSub_button().add(new PicPhotoOrAlbumButton("传图", "31"));
        sb.getSub_button().add(new ClickButton("点击", "32"));
        sb.getSub_button().add(new ViewButton("网易", "http://news.163.com"));
        //加入第三个一级菜单
        btn.getButton().add(sb);
        //转为json
        JSONObject jsonObject = JSONObject.fromObject(btn);
        System.out.println(jsonObject.toString());
    }

    /**
     * 获取token
     */
    @Test
    public void testToken() {
        System.out.println(WxService.getAccessToken());
        System.out.println(WxService.getAccessToken());
    }

    @Test
    public void testMsg() {
        Map<String, String> map = new HashMap<>();
        map.put("ToUserName", "to");
        map.put("FromUserName", "from");
        map.put("MsgType", "type");
        TextMessage text = new TextMessage(map, "aaaaaaaa");
        XStream stream = new XStream();
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);
        stream.processAnnotations(ImageMessage.class);
        String xml = stream.toXML(text);
        System.out.println(xml);
    }
}

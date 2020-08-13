package com.example.demo.util;

import com.example.demo.Entity.*;
import com.example.demo.WxService;
import net.sf.json.JSONObject;


public class CreateMenu {
    public static String menuReasult() {
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

        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", WxService.getAccessToken());
        //发送请求
        String data = jsonObject.toString();
        data = data.replace("，", ",");
        String result = Util.post(url, data);
        return result;
    }
}

package com.example.demo;

import com.example.demo.util.Util;
import net.sf.json.JSONObject;
import org.aspectj.bridge.MessageUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/test")
public class testController {

    @RequestMapping("/index")
    public String sayHello() {
        return "index";
    }

    /**
     * 网页授权获取用户信息
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    protected void getUserInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取code
        String code = req.getParameter("code");
        //获取access_token的地址
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url = url.replace("APPID", "wx8cd79f97cf30af53");
        url = url.replace("SECRET", "150ae6ee51f9a2e8dc1a88255ba20f16");
        url = url.replace("CODE", code);
        String result = Util.get(url);
        String token = JSONObject.fromObject(result).getString("access_token");
        String openId = JSONObject.fromObject(result).getString("openid");
        //拉取用户的基本信息
        String strUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        strUrl = strUrl.replace("ACCESS_TOKEN", token);
        strUrl = strUrl.replace("OPENID", openId);
        String strResilt = Util.get(strUrl);
        System.out.println(strResilt);
    }

    @RequestMapping(value = "/verify_wx_token", method = RequestMethod.GET)
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String signature = req.getParameter("signature");
        //timestamp	时间戳
        String timestamp = req.getParameter("timestamp");
        //nonce	随机数
        String nonce = req.getParameter("nonce");
        //echostr	随机字符串
        String echostr = req.getParameter("echostr");
        if (WxService.check(timestamp, nonce, signature)) {
            PrintWriter out = resp.getWriter();
            //原因返回echostr参数
            out.print(echostr);
            out.flush();
            out.close();
        } else {
            System.out.println("接入失败");
        }
    }

    //接收消息和事件推送
    @RequestMapping(value = "/verify_wx_token", method = RequestMethod.POST)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("utf8");
        resp.setCharacterEncoding("utf8");
        //处理消息和事件推送
        Map<String, String> requestMap = WxService.parseRequest(req.getInputStream());
        //准备回复的数据包
        String respXml = WxService.getResponse(requestMap);
        PrintWriter out = resp.getWriter();
        out.print(respXml);
        out.flush();
        out.close();
        System.out.println(requestMap);
    }
}

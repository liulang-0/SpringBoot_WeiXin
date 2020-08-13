package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/wx")
public class WxServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @RequestMapping("/TestGetMethod")
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

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("post");
    }
}

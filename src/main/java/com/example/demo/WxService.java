package com.example.demo;


import com.baidu.aip.ocr.AipOcr;
import com.example.demo.Entity.*;
import com.example.demo.util.Util;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class WxService {
    //设置APPID/AK/SK
    public static final String APP_ID = "21945987";//你的 App ID
    public static final String API_KEY = "nSvkLPm8mQ30h6UWYQPZNHMK";//你的 Api Key
    public static final String SECRET_KEY = "ffCljnV55DG4zbHE1PU6N1aWBwHdfMS1";//你的 Secret Key

    //微信接口配置信息token
    private static final String TOKEN = "abc";
    //微信公众号
    private static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static final String APPID = "wx8cd79f97cf30af53";
    private static final String APPSECRET = "150ae6ee51f9a2e8dc1a88255ba20f16";
    //存储token对象
    private static AccessToken at;

    /**
     * 获取token
     */
    private static void getToken() {
        String url = GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
        String tokenStr = Util.get(url);
        JSONObject jsonObject = JSONObject.fromObject(tokenStr);
        String token = jsonObject.getString("access_token");
        String expiresIn = jsonObject.getString("expires_in");
        //创建token并保存
        at = new AccessToken(token, expiresIn);
    }

    /**
     * 对外获取token的方法
     *
     * @return
     */
    public static String getAccessToken() {
        if (at == null || at.isExpired()) {
            getToken();
        }
        return at.getAccessToken();
    }

    /**
     * 验证签名
     *
     * @param timestamp
     * @param nonce
     * @param signature
     * @return
     */
    public static boolean check(String timestamp, String nonce, String signature) {
        //1）将token、timestamp、nonce三个参数进行字典序排序
        String[] strs = new String[]{TOKEN, timestamp, nonce};
        Arrays.sort(strs);
        // 2）将三个参数字符串拼接成一个字符串进行sha1加密
        String str = strs[0] + strs[1] + strs[2];
        String mysig = sha1(str);
        // 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        return mysig.equalsIgnoreCase(signature);
    }

    /**
     * sha1加密
     * @param src
     * @return
     */
    private static String sha1(String src) {

        try {
            //获取一个加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");
            //加密
            byte[] digest = md.digest(src.getBytes());
            char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            StringBuilder sb = new StringBuilder();
            //处理加密结果
            for (byte b : digest) {
                sb.append(chars[(b >> 4) & 15]);
                sb.append(chars[b & 15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析xml数据包
     *
     * @param inputStream
     * @return
     */
    public static Map<String, String> parseRequest(ServletInputStream inputStream) {
        SAXReader reader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        try {
            //读取输入流获取文档对象
            Document document = reader.read(inputStream);
            //根据文档对象获取根节点
            Element root = document.getRootElement();
            //获取根节点的子节点
            List<Element> elements = root.elements();
            for (Element e : elements) {
                map.put(e.getName(), e.getStringValue());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 处理所有事件和消息回复
     *
     * @param requestMap
     * @return 返回xml数据包
     */
    public static String getResponse(Map<String, String> requestMap) {
        BaseMessage msg = null;
        String msgType = requestMap.get("MsgType");
        switch (msgType) {
            case "text":
                msg = dealTextMessage(requestMap);
                break;
            case "event":
                msg = dealEvent(requestMap);
                break;
            case "image":
                msg = dealImage(requestMap);
                break;
            default:
                break;
        }
        if (msg != null)
            return beanToXml(msg);
        else return null;
    }

    /**
     * 传图处理
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealImage(Map<String, String> requestMap) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
//        // 本地图片调用接口
//        String path = "test.jpg";
//        org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        //网络图片调用接口
        String path = requestMap.get("PicUrl");
        org.json.JSONObject res = client.generalUrl(path, new HashMap<String, String>());

        String json = res.toString();
        //转为JSONObject
        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("words_result");
        Iterator<JSONObject> it = jsonArray.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            JSONObject next = it.next();
            sb.append(next.getString("words"));
        }
        return new TextMessage(requestMap, sb.toString());

    }

    /**
     * 处理事件推送
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealEvent(Map<String, String> requestMap) {
        String event = requestMap.get("Event");
        switch (event) {
            case "CLICK":
                return dealClick(requestMap);
            case "View":
                return dealView(requestMap);
            default:
                break;
        }
        return null;
    }

    /**
     * 处理view类型的菜单
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealView(Map<String, String> requestMap) {
        return null;
    }

    /**
     * 处理click类型的菜单
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealClick(Map<String, String> requestMap) {
        String key = requestMap.get("EventKey");
        switch (key) {
            //点击第一个一级菜单
            case "1":
                //处理点击第一个一级菜单
                return new TextMessage(requestMap, "你点击的是第一个一级菜单");
            case "32":
                //处理点击第三个一级菜单的第二个子菜单
                return new TextMessage(requestMap, "你点击的是第三个一级菜单的第二个子菜单");
        }
        return null;
    }

    /**
     * 把消息对象处理为xml数据包
     *
     * @param msg
     * @return
     */
    private static String beanToXml(BaseMessage msg) {
        XStream stream = new XStream();
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);
        stream.processAnnotations(ImageMessage.class);
        String xml = stream.toXML(msg);
        return xml;
    }

    /**
     * 处理文本消息
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealTextMessage(Map<String, String> requestMap) {

        String msg = requestMap.get("Content");
        if (msg.equals("登录")) {
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE#wechat_redirect";
            url = url.replace("APPID", "wx8cd79f97cf30af53");
            String redirectUrl = "http://f71133075c98.ngrok.io/api/test/getUserInfo";
            url = url.replace("REDIRECT_URI", redirectUrl);
            url = url.replace("SCOPE", "snsapi_userinfo");
            TextMessage tm = new TextMessage(requestMap, "点击<a href=\"" + url + "\">这里</a>登录");
            return tm;
        }
        TextMessage tm = new TextMessage(requestMap, "aaaa");
        return tm;
    }

    /**
     * 上传临时素材  https  POST请求
     *
     * @param path 文件路径
     * @param type 文件类型
     * @return
     */
    public static String uploadPostWithFile(String path, String type) {
        File file = new File(path);
        //接口请求地址
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
        url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("TYPE", type);
        try {
            URL urlObj = new URL(url);
            //强转为安全链接
            HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
            //设置链接信息
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //设置请求头信息
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "utf8");
            //数据边界
            String boundary = "-----" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //获取输出流
            OutputStream outputStream = conn.getOutputStream();
            //创建文件输入流
            InputStream inputStream = new FileInputStream(file);
            //第一部分：头部信息
            StringBuilder strTop = new StringBuilder();
            strTop.append("--");
            strTop.append(boundary);
            strTop.append("\r\n");
            strTop.append("Content-Disposition:form-data; name=\"media\"; filename=\"" + file.getName() + "\"\r\n");
            strTop.append("Content-Type: application/octet-stream\r\n\r\n");
            System.out.println(strTop.toString());
            outputStream.write(strTop.toString().getBytes());
            //第二部分：文件内容
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            //第三部分：尾部信息
            String foot = "\r\n--" + boundary + "--\r\n";
            outputStream.write(foot.getBytes());
            outputStream.flush();
            outputStream.close();
            //读取数据
            InputStream is = conn.getInputStream();
            StringBuilder resp = new StringBuilder();
            while ((len = is.read(b)) != -1) {
                resp.append(new String(b, 0, len));
            }
            return resp.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取带参数的二维码的ticket
     *
     * @return
     */
    public static String getQrCodeTicket() {
        /**
         * 获取二维码的方式
         * https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET
         */

        String token = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
        url = url.replace("TOKEN", token);
        //生成临时的字符二维码
        String data = "{\"expire_seconds\": 600, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
        String result = Util.post(url, data);
        String ticket = JSONObject.fromObject(result).getString("ticket");
        return ticket;

    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static String getUserInfo(String openId) {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("OPENID", openId);
        String result = Util.get(url);
        return result;
    }

}

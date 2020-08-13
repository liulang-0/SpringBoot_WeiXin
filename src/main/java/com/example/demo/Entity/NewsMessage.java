package com.example.demo.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;
import java.util.Map;

@XStreamAlias("xml")
public class NewsMessage extends BaseMessage {
    private String ArticleCount;
    private List<Article> articleList;

    public String getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(String articleCount) {
        ArticleCount = articleCount;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public NewsMessage(Map<String, String> requestMap, List<Article> articleList, String articleCount) {
        super(requestMap);
        //设置消息类型
        this.setMsgType("music");
        this.setArticleCount(articleCount);
        this.setArticleList(articleList);
    }
}

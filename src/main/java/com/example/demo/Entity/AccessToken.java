package com.example.demo.Entity;

public class AccessToken {
    private String accessToken;
    private long expireTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public AccessToken(String accessToken, String expireIn) {
        super();
        this.setAccessToken(accessToken);
        this.setExpireTime(System.currentTimeMillis() + Integer.parseInt(expireIn) * 1000);
    }

    /**
     * 判断token是否过期
     * @return
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}

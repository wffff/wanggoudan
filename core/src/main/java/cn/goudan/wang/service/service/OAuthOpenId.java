package cn.goudan.wang.service.service;

import cn.goudan.wang.baseconfig.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by momo on 2017/5/1.
 */
public class OAuthOpenId {

    private static final String OPENID_PREFIX = "bring_";

    @JsonProperty("openid")
    private String openId;
    @JsonProperty("client_id")
    private String clientId;
    @JsonIgnore
    private Long uid;

    public OAuthOpenId(String clientId, Long uid) {
        this(makeOpenId(clientId, uid), clientId, uid);
    }

    public OAuthOpenId(String openId, String clientId, Long uid) {
        this.openId = openId;
        this.clientId = clientId;
        this.uid = uid;
    }

    public String getOpenId() {
        return openId;
    }

    public String getClientId() {
        return clientId;
    }

    public Long getUid() {
        return uid;
    }

    private static String makeOpenId(String clientId, Long uid) {
        return Utils.extractTokenKey(OPENID_PREFIX + clientId + "_" + String.valueOf(uid));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthOpenId that = (OAuthOpenId) o;
        return Objects.equals(openId, that.openId) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openId, clientId, uid);
    }

    @Override
    public String toString() {
        return "OAuthOpenId{" +
                "openId='" + openId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", uid=" + uid +
                '}';
    }
}

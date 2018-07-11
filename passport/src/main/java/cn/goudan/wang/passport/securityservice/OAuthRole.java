package cn.goudan.wang.passport.securityservice;

/**
 * Created by MOMO-PC on 2017/5/10.
 */
public class OAuthRole {

    private Long id;
    private String name;
    private String description;

    public OAuthRole() {
    }

    public OAuthRole(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

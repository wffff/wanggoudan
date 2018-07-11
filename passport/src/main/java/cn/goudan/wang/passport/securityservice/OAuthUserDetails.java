package cn.goudan.wang.passport.securityservice;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * Created by momo on 2017/4/20.
 */
public class OAuthUserDetails implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private Long id;
    private String username;
    private String password;
    private String mobile;
    private String email;
    private String name;
    private boolean enabled;
    private boolean expired;
    private boolean locked;
    private boolean limited;
    private Date time;
    private Collection<? extends GrantedAuthority> authorities;
    public OAuthUserDetails(){

    }

    public OAuthUserDetails(Long id, String username, String password, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this(id, username, password, enabled, false, false, false, authorities);
    }

    public OAuthUserDetails(Long id, String username, String password, boolean enabled, boolean expired, boolean locked, boolean limited, Collection<? extends GrantedAuthority> authorities) {
        this(id, username, password, null, null, null, enabled, expired, locked, limited, null, authorities);
    }

    public OAuthUserDetails(Long id, String username, String password, String mobile, String email, String name, boolean enabled, boolean expired, boolean locked, boolean limited, Date time, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
        this.email = email;
        this.name = name;
        this.enabled = enabled;
        this.expired = expired;
        this.locked = locked;
        this.limited = limited;
        this.time = time;
        this.authorities = authorities;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !limited;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
}

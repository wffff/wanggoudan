package cn.goudan.wang.service.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.Jackson2ArrayOrStringDeserializer;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by momo on 2017/4/23.
 */
public class OAuthClientDetails implements ClientDetails {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> scope = Collections.emptySet();
    @JsonProperty("resource_ids")
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> resourceIds = Collections.emptySet();
    @JsonProperty("authorized_grant_types")
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> authorizedGrantTypes = Collections.emptySet();
    @JsonProperty("redirect_uri")
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> registeredRedirectUris;
    @JsonProperty("autoapprove")
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> autoApproveScopes;
    private List<GrantedAuthority> authorities = Collections.emptyList();
    @JsonProperty("access_token_validity")
    private Integer accessTokenValiditySeconds;
    @JsonProperty("refresh_token_validity")
    private Integer refreshTokenValiditySeconds;
    private Map<String, Object> additionalInformation = new LinkedHashMap<>();
    private Long uid;
    private Integer type;
    private String site;
    private String url;
    private Boolean enabled;
    private Timestamp last;
    private Timestamp time;

    public OAuthClientDetails() {
    }

    public OAuthClientDetails(ClientDetails prototype) {
        setAccessTokenValiditySeconds(prototype.getAccessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(prototype.getRefreshTokenValiditySeconds());
        setAuthorities(prototype.getAuthorities());
        setAuthorizedGrantTypes(prototype.getAuthorizedGrantTypes());
        setClientId(prototype.getClientId());
        setClientSecret(prototype.getClientSecret());
        setRegisteredRedirectUri(prototype.getRegisteredRedirectUri());
        setScope(prototype.getScope());
        setResourceIds(prototype.getResourceIds());
    }

    public OAuthClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, String authorities) {
        this(clientId, resourceIds, scopes, grantTypes, authorities, null);
    }

    public OAuthClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, String authorities, String redirectUris) {

        this.clientId = clientId;

        if (StringUtils.hasText(resourceIds)) {
            Set<String> resources = StringUtils.commaDelimitedListToSet(resourceIds);
            if (!resources.isEmpty()) this.resourceIds = resources;
        }

        if (StringUtils.hasText(scopes)) {
            Set<String> scopeList = StringUtils.commaDelimitedListToSet(scopes);
            if (!scopeList.isEmpty()) this.scope = scopeList;
        }

        if (StringUtils.hasText(grantTypes)) {
            this.authorizedGrantTypes = StringUtils.commaDelimitedListToSet(grantTypes);
        } else {
            this.authorizedGrantTypes = new HashSet<>(Arrays.asList("authorization_code", "refresh_token"));
        }

        if (StringUtils.hasText(authorities)) this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        if (StringUtils.hasText(redirectUris)) this.registeredRedirectUris = StringUtils.commaDelimitedListToSet(redirectUris);
    }

    @Override
    @JsonIgnore
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    @JsonIgnore
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = resourceIds == null ? Collections.emptySet() : new LinkedHashSet<>(resourceIds);
    }

    @Override
    @JsonIgnore
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    @JsonIgnore
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    @JsonIgnore
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Collection<String> scope) {
        this.scope = scope == null ? Collections.emptySet() : new LinkedHashSet<>(scope);
    }

    @Override
    @JsonIgnore
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = new LinkedHashSet<>(authorizedGrantTypes);
    }

    @Override
    @JsonIgnore
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUris;
    }

    public void setRegisteredRedirectUri(Collection<String> redirectUri) {
        this.registeredRedirectUris = redirectUri == null ? null : new LinkedHashSet<>(redirectUri);
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new ArrayList<>(authorities);
    }

    @JsonProperty("authorities")
    private List<String> getAuthoritiesAsStrings() {
        return new ArrayList<>(AuthorityUtils.authorityListToSet(authorities));
    }

    @JsonProperty("authorities")
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private void setAuthoritiesAsStrings(Set<String> values) {
        setAuthorities(AuthorityUtils.createAuthorityList(values.toArray(new String[values.size()])));
    }

    @Override
    @JsonIgnore
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    @JsonIgnore
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {

        if (autoApproveScopes == null) return false;

        for (String auto : autoApproveScopes) {
            if (auto.equals("true") || scope.matches(auto)) return true;
        }

        return false;
    }

    @JsonIgnore
    public Set<String> getAutoApproveScopes() {
        return autoApproveScopes;
    }

    public void setAutoApproveScopes(Collection<String> autoApproveScopes) {
        this.autoApproveScopes = new HashSet<>(autoApproveScopes);
    }

    @Override
    @JsonAnyGetter
    public Map<String, Object> getAdditionalInformation() {
        return Collections.unmodifiableMap(this.additionalInformation);
    }

    public void setAdditionalInformation(Map<String, ?> additionalInformation) {
        this.additionalInformation = new LinkedHashMap<>(additionalInformation);
    }

    @JsonAnyGetter
    public void addAdditionalInformation(String key, Object value) {
        this.additionalInformation.put(key, value);
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isEnabled() {
        return getEnabled();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getLast() {
        return last;
    }

    public void setLast(Timestamp last) {
        this.last = last;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthClientDetails that = (OAuthClientDetails) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientSecret, that.clientSecret) &&
                Objects.equals(scope, that.scope) &&
                Objects.equals(resourceIds, that.resourceIds) &&
                Objects.equals(authorizedGrantTypes, that.authorizedGrantTypes) &&
                Objects.equals(registeredRedirectUris, that.registeredRedirectUris) &&
                Objects.equals(autoApproveScopes, that.autoApproveScopes) &&
                Objects.equals(authorities, that.authorities) &&
                Objects.equals(accessTokenValiditySeconds, that.accessTokenValiditySeconds) &&
                Objects.equals(refreshTokenValiditySeconds, that.refreshTokenValiditySeconds) &&
                Objects.equals(additionalInformation, that.additionalInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientSecret, scope, resourceIds, authorizedGrantTypes, registeredRedirectUris, autoApproveScopes, authorities, accessTokenValiditySeconds, refreshTokenValiditySeconds, additionalInformation);
    }

    @Override
    public String toString() {
        return "OAuthClientDetails{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", scope=" + scope +
                ", resourceIds=" + resourceIds +
                ", authorizedGrantTypes=" + authorizedGrantTypes +
                ", registeredRedirectUris=" + registeredRedirectUris +
                ", autoApproveScopes=" + autoApproveScopes +
                ", authorities=" + authorities +
                ", accessTokenValiditySeconds=" + accessTokenValiditySeconds +
                ", refreshTokenValiditySeconds=" + refreshTokenValiditySeconds +
                ", additionalInformation=" + additionalInformation +
                '}';
    }
}

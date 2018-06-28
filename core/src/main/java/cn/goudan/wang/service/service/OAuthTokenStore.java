package cn.goudan.wang.service.service;

import cn.goudan.wang.baseconfig.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by momo on 2017/4/24.
 */
public class OAuthTokenStore implements TokenStore {

    private static final Log LOG = LogFactory.getLog(OAuthTokenStore.class);

    // access token
    private static final String DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT = "INSERT INTO oauth_access_token (token_id, token, authentication_id, username, client_id, authentication, refresh_token) VALUES (?,?,?,?,?,?,?)";
    private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "SELECT token_id, token FROM oauth_access_token WHERE token_id=?";
    private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "SELECT token_id, authentication FROM oauth_access_token WHERE token_id=?";
    private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "SELECT token_id, token FROM oauth_access_token WHERE authentication_id=?";
    private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT = "SELECT token_id, token FROM oauth_access_token WHERE username=? AND client_id=?";
    private static final String DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT = "SELECT token_id, token FROM oauth_access_token WHERE client_id=?";
    private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT = "SELECT token_id, token FROM oauth_access_token WHERE username=?";
    private static final String DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT = "DELETE FROM oauth_access_token WHERE token_id=?";
    private static final String DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT = "DELETE FROM oauth_access_token WHERE refresh_token=?";
    // refresh token
    private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "INSERT INTO oauth_refresh_token (token_id, token, authentication) VALUES (?,?,?)";
    private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "SELECT token_id, token FROM oauth_refresh_token WHERE token_id=?";
    private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "SELECT token_id, authentication FROM oauth_refresh_token WHERE token_id=?";
    private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "DELETE FROM oauth_refresh_token WHERE token_id=?";

    private AuthenticationKeyGenerator authenticationKeyGenerator = new OAuthAuthenticationKeyGenerator();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {

        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String tokenValue) {

        try {
            OAuth2Authentication authentication = jdbcTemplate.queryForObject(DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT, (rs, rowNum) -> Utils.deserializeAuthentication(rs.getBytes("authentication")), tokenValue);
            return authentication;
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for token " + tokenValue);
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize authentication for " + tokenValue, e);
            removeAccessToken(tokenValue);
        }

        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

        String refreshTokenValue = null;
        if (token.getRefreshToken() != null) refreshTokenValue = token.getRefreshToken().getValue();
        if (readAccessToken(token.getValue()) != null) removeAccessToken(token.getValue());

        try {
            jdbcTemplate.update(DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT,
                    new Object[]{
                            token.getValue(),
                            new SqlLobValue(Utils.serializeAccessToken(token)),
                            authenticationKeyGenerator.extractKey(authentication),
                            authentication.isClientOnly() ? null : authentication.getName(),
                            authentication.getOAuth2Request().getClientId(),
                            new SqlLobValue(Utils.serializeAuthentication(authentication)),
                            refreshTokenValue
                    },
                    new int[]{Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR}
            );
        } catch (DuplicateKeyException e) {
            //throw new ClientAlreadyExistsException("authorization code already exists: " + code, e);
        }
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {

        try {
            return jdbcTemplate.queryForObject(DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT, (rs, rowNum) -> Utils.deserializeAccessToken(rs.getBytes("token")), tokenValue);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for token " + tokenValue);
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for " + tokenValue, e);
            removeAccessToken(tokenValue);
        }

        return null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {

        removeAccessToken(token.getValue());
    }


    public void removeAccessToken(String tokenValue) {

        jdbcTemplate.update(DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT, tokenValue);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {

        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(String refreshTokenValue) {

        jdbcTemplate.update(DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT, new Object[]{refreshTokenValue}, new int[]{Types.VARCHAR});
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {

        OAuth2AccessToken accessToken = null;
        String authenticationKey = authenticationKeyGenerator.extractKey(authentication);

        try {
            accessToken = jdbcTemplate.queryForObject(DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT, (rs, rowNum) -> Utils.deserializeAccessToken(rs.getBytes("token")), authenticationKey);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for token " + accessToken);
        } catch (IllegalArgumentException e) {
            LOG.error("Could not extract access token for authentication " + authentication, e);
        }

        if (accessToken != null && !authenticationKey.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            storeAccessToken(accessToken, authentication); // Keep the store consistent (maybe the same user is represented by this authentication but the details have changed)
        }

        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {

        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = jdbcTemplate.query(DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT, (rs, rowNum) -> mapRow(rs), userName, clientId);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for clientId " + clientId + " and userName " + userName);
        }

        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {

        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = jdbcTemplate.query(DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT, (rs, rowNum) -> mapRow(rs), clientId);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for clientId " + clientId);
        }

        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {

        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = jdbcTemplate.query(DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT, (rs, rowNum) -> mapRow(rs), userName);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for userName " + userName);
        }

        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

        try {
            jdbcTemplate.update(DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT,
                    new Object[]{
                            refreshToken.getValue(),
                            new SqlLobValue(Utils.serializeRefreshToken(refreshToken)),
                            new SqlLobValue(Utils.serializeAuthentication(authentication))
                    },
                    new int[]{Types.VARCHAR, Types.BLOB, Types.BLOB}
            );
        } catch (DuplicateKeyException e) {
            //throw new ClientAlreadyExistsException("authorization code already exists: " + code, e);
        }
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {

        try {
            return jdbcTemplate.queryForObject(DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT, (rs, rowNum) -> Utils.deserializeRefreshToken(rs.getBytes("token")), tokenValue);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find refresh token for token " + tokenValue);
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize refresh token for token " + tokenValue, e);
            removeRefreshToken(tokenValue);
        }

        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {

        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {

        try {
            return jdbcTemplate.queryForObject(DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT, (rs, rowNum) -> Utils.deserializeAuthentication(rs.getBytes("authentication")), tokenValue);
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isInfoEnabled()) LOG.info("Failed to find access token for token " + tokenValue);
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for " + tokenValue, e);
            removeRefreshToken(tokenValue);
        }

        return null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {

        removeRefreshToken(token.getValue());
    }

    public void removeRefreshToken(String tokenValue) {

        jdbcTemplate.update(DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT, tokenValue);
    }

    private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {

        List<OAuth2AccessToken> tokens = new ArrayList<>();

        for (OAuth2AccessToken token : accessTokens) {
            if (token != null) tokens.add(token);
        }

        return tokens;
    }

    private OAuth2AccessToken mapRow(ResultSet rs) throws SQLException {

        try {
            return Utils.deserializeAccessToken(rs.getBytes("token"));
        } catch (IllegalArgumentException e) {
            removeAccessToken(rs.getString("token_id"));
        }

        return null;
    }
}

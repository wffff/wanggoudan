package cn.goudan.wang.passport.securityservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by momo on 2017/4/23.
 */
@Service
public class OAuthClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private static final String SELECT_CLIENT_DETAILS_BY_CLIENT_ID = "SELECT client_id, client_secret, resource_ids, scope, grant_types, redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, uid, type, site, url, enabled, last, time FROM oauth_client WHERE client_id=? AND enabled=TRUE";
    private static final String LIST_CLIENT_DETAILS = "SELECT client_id, client_secret, resource_ids, scope, grant_types, redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, uid, type, site, url, enabled, last, time FROM oauth_client ORDER BY client_id";
    private static final String ADD_CLIENT_DETAILS = "INSERT INTO oauth_client (client_secret, resource_ids, scope, grant_types, redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, client_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String ADD_CLIENT_DETAILS_OAUTH = "INSERT INTO oauth_client (client_secret,type,site,url,uid,enabled, resource_ids, scope, grant_types, redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, client_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CLIENT_DETAILS2 = "UPDATE oauth_client SET type=?, site=?, url=?, uid=?, enabled=?, resource_ids=?, scope=?, grant_types=?, redirect_uri=?, authorities=?, access_token_validity=?, refresh_token_validity=?, additional_information=?, autoapprove=? WHERE client_id=?";
    private static final String UPDATE_CLIENT_DETAILS = "UPDATE oauth_client SET resource_ids=?, scope=?, grant_types=?, redirect_uri=?, authorities=?, access_token_validity=?, refresh_token_validity=?, additional_information=?, autoapprove=? WHERE client_id=?";
    private static final String UPDATE_CLIENT_SECRET = "UPDATE oauth_client SET client_secret=? WHERE client_id=?";
    private static final String REMOVE_CLIENT_DETAILS = "DELETE FROM oauth_client WHERE client_id=?";
    // ext
    private static final String GET_CLIENTS = "SELECT client_id, client_secret, resource_ids, scope, grant_types, redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, uid, type, site, url, enabled, last, time FROM oauth_client ORDER BY time DESC ";
    private static final String GET_CLIENTS_TOTAL = "SELECT COUNT(client_id) FROM oauth_client";
    private static final String GET_BY_CLIENT_ID = "SELECT client_id, client_secret, resource_ids, scope, grant_types, redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, uid, type, site, url, enabled, last, time FROM oauth_client WHERE client_id=?";


    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {

        Assert.hasText(clientId, "no client id");

        try {
            return jdbcTemplate.queryForObject(SELECT_CLIENT_DETAILS_BY_CLIENT_ID, (rs, rowNum) -> mapRow(rs), clientId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchClientException("no client by clientId: " + clientId);
        }
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {

        Assert.notNull(clientDetails, "no client id");

        Object[] fieldsForUpdate = getFieldsForUpdate(clientDetails);
        Object[] fields = new Object[fieldsForUpdate.length + 1];
        System.arraycopy(fieldsForUpdate, 0, fields, 1, fieldsForUpdate.length);
        fields[0] = clientDetails.getClientSecret() != null ? passwordEncoder.encode(clientDetails.getClientSecret()) : null;

        try {
            jdbcTemplate.update(ADD_CLIENT_DETAILS, fields);
        } catch (DuplicateKeyException e) {
            throw new ClientAlreadyExistsException("client already exists: " + clientDetails.getClientId());
        }
    }

    public void addOauthClientDetails(OAuthClientDetails clientDetails) throws ClientAlreadyExistsException {

        Assert.notNull(clientDetails, "no client id");

        Object[] fieldsForUpdate = getFieldsForUpdateOAuth(clientDetails);
        Object[] fields = new Object[fieldsForUpdate.length + 1];
        System.arraycopy(fieldsForUpdate, 0, fields, 1, fieldsForUpdate.length);
        fields[0] = clientDetails.getClientSecret() != null ? passwordEncoder.encode(clientDetails.getClientSecret()) : null;

        try {
            jdbcTemplate.update(ADD_CLIENT_DETAILS_OAUTH, fields);
        } catch (DuplicateKeyException e) {
            throw new ClientAlreadyExistsException("client already exists: " + clientDetails.getClientId());
        }
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {

        Assert.notNull(clientDetails, "no client id");

        int count = jdbcTemplate.update(UPDATE_CLIENT_DETAILS, getFieldsForUpdate(clientDetails));
        if (count != 1) throw new NoSuchClientException("no client found with id = " + clientDetails.getClientId());
    }

    public void updateOAuthClientDetails(OAuthClientDetails clientDetails) throws NoSuchClientException {
        Assert.notNull(clientDetails, "no client id");
        int count = jdbcTemplate.update(UPDATE_CLIENT_DETAILS2, getFieldsForUpdateOAuth(clientDetails));
        if (count != 1) throw new NoSuchClientException("no client found with id = " + clientDetails.getClientId());
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {

        Assert.hasText(clientId, "no client id");

        int count = jdbcTemplate.update(UPDATE_CLIENT_SECRET, passwordEncoder.encode(secret), clientId);
        if (count != 1) throw new NoSuchClientException("no client found with id = " + clientId);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {

        Assert.hasText(clientId, "no client id");

        int count = jdbcTemplate.update(REMOVE_CLIENT_DETAILS, clientId);
        if (count != 1) throw new NoSuchClientException("no client found with id = " + clientId);
    }

    @Override
    public List<ClientDetails> listClientDetails() {

        return jdbcTemplate.query(LIST_CLIENT_DETAILS, (rs, rowNum) -> mapRow(rs));
    }

    private Object[] getFieldsForUpdate(ClientDetails clientDetails) {

        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(clientDetails.getAdditionalInformation());
        } catch (Exception e) {
            //logger.warn("Could not serialize additional information: " + clientDetails, e);
        }

        return new Object[]{
                clientDetails.getResourceIds() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getResourceIds()) : null,
                clientDetails.getScope() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getScope()) : null,
                clientDetails.getAuthorizedGrantTypes() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorizedGrantTypes()) : null,
                clientDetails.getRegisteredRedirectUri() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getRegisteredRedirectUri()) : null,
                clientDetails.getAuthorities() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorities()) : null, clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                json,
                getAutoApproveScopes(clientDetails),
                clientDetails.getClientId()
        };
    }

    private Object[] getFieldsForUpdateOAuth(OAuthClientDetails clientDetails) {

        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(clientDetails.getAdditionalInformation());
        } catch (Exception e) {
            //logger.warn("Could not serialize additional information: " + clientDetails, e);
        }

        return new Object[]{
                clientDetails.getType() != null ? clientDetails.getType() : null,
                clientDetails.getSite() != null ? clientDetails.getSite() : null,
                clientDetails.getUrl() != null ? clientDetails.getUrl() : null,
                clientDetails.getUid() != null ? clientDetails.getUid() : null,
                clientDetails.getEnabled(),
                clientDetails.getResourceIds() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getResourceIds()) : null,
                clientDetails.getScope() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getScope()) : null,
                clientDetails.getAuthorizedGrantTypes() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorizedGrantTypes()) : null,
                clientDetails.getRegisteredRedirectUri() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getRegisteredRedirectUri()) : null,
                clientDetails.getAuthorities() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorities()) : null, clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                json,
                getAutoApproveScopes(clientDetails),
                clientDetails.getClientId()
        };
    }

    private OAuthClientDetails mapRow(ResultSet rs) throws SQLException {

        OAuthClientDetails details = new OAuthClientDetails(
                rs.getString("client_id"),
                rs.getString("resource_ids"),
                rs.getString("scope"),
                rs.getString("grant_types"),
                rs.getString("authorities"),
                rs.getString("redirect_uri")
        );

        details.setClientSecret(rs.getString("client_secret"));
        if (rs.getObject("access_token_validity") != null)
            details.setAccessTokenValiditySeconds(rs.getInt("access_token_validity"));
        if (rs.getObject("refresh_token_validity") != null)
            details.setRefreshTokenValiditySeconds(rs.getInt("refresh_token_validity"));

        String json = rs.getString("additional_information");
        if (json != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Object> additionalInformation = mapper.readValue(json, Map.class);
                details.setAdditionalInformation(additionalInformation);
            } catch (Exception e) {
                //logger.warn("Could not decode JSON for additional information: " + details, e);
            }
        }

        String scopes = rs.getString("autoapprove");
        if (scopes != null) details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(scopes));

        details.setUid(rs.getLong("uid"));
        details.setType(rs.getInt("type"));
        details.setSite(rs.getString("site"));
        details.setUrl(rs.getString("url"));
        details.setEnabled(rs.getBoolean("enabled"));
        details.setLast(rs.getTimestamp("last"));
        details.setTime(rs.getTimestamp("time"));

        return details;
    }

    private String getAutoApproveScopes(ClientDetails clientDetails) {

        if (clientDetails.isAutoApprove("true")) return "true"; // all scopes autoapproved

        Set<String> scopes = new HashSet<>();

        for (String scope : clientDetails.getScope()) {

            if (clientDetails.isAutoApprove(scope)) scopes.add(scope);
        }

        return StringUtils.collectionToCommaDelimitedString(scopes);
    }

    // ext
    public List<OAuthClientDetails> getClients(Integer page, Integer rows) {
        if (page == null) page = 1;
        if (rows == null) rows = 20;
        int limit = 0;
        int offset = 0;
        int count = jdbcTemplate.queryForObject(GET_CLIENTS_TOTAL, Integer.class);
        int pages = rows == 0 ? 0 : (count / rows);
        if (count % rows != 0) pages++;
        if (page < 1) page = 1;
        if (page > pages) page = pages;
        int start = (page - 1) * rows;
        if (count > 0 && start < count) {
            offset = start;
            limit = rows;
        }
        String Page = " LIMIT " + limit + " OFFSET " + offset + "";
        return jdbcTemplate.query(GET_CLIENTS + Page, (rs, rowNum) -> mapRow(rs));
    }

    public OAuthClientDetails getByClientId(String clientId) throws InvalidClientException {

        if (!StringUtils.hasText(clientId)) throw new InvalidClientException("no client id");

        try {
            return jdbcTemplate.queryForObject(GET_BY_CLIENT_ID, (rs, rowNum) -> mapRow(rs), clientId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchClientException("no client by clientId: " + clientId);
        }
    }

    public Integer getClientsCount() {

        return jdbcTemplate.queryForObject(GET_CLIENTS_TOTAL, Integer.class);
    }
}

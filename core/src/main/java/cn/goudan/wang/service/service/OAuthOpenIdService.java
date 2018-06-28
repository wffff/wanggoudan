package cn.goudan.wang.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by momo on 2017/5/1.
 */
@Service
public class OAuthOpenIdService {

    private static final String GET_OPENID = "SELECT openid, client_id, uid FROM oauth_openid WHERE client_id=? AND uid=?";
    private static final String ADD_OPENID = "INSERT INTO oauth_openid (openid, client_id, uid) VALUES (?,?,?)";
    private static final String REMOVE_OPENID = "DELETE FROM oauth_openid WHERE openid=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public OAuthOpenId getOpenId(String clientId, Long uid) throws NoSuchOpenIdException {

        Assert.hasText(clientId, "no client id");
        Assert.notNull(uid, "no uid");

        try {
            return jdbcTemplate.queryForObject(GET_OPENID, (rs, rowNum) -> mapRow(rs), clientId, uid);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchOpenIdException("no openid found");
        }
    }

    public void createOpenId(OAuthOpenId openId) throws OpenIdAlreadyExistsException {

        Assert.notNull(openId, "no openid");

        try {
            jdbcTemplate.update(ADD_OPENID, new Object[]{openId.getOpenId(), openId.getClientId(), openId.getUid()});
        } catch (DuplicateKeyException e) {
            throw new OpenIdAlreadyExistsException("openid already exists");
        }
    }

    public void removeOpenId(String openIdValue) throws NoSuchOpenIdException {

    }

    private OAuthOpenId mapRow(ResultSet rs) throws SQLException {

        return new OAuthOpenId(
                rs.getString("openid"),
                rs.getString("client_id"),
                rs.getLong("uid")
        );
    }
}

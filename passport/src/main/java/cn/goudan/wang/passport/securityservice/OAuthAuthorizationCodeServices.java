package cn.goudan.wang.passport.securityservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Types;

/**
 * Created by momo on 2017/4/24.
 */
@Service
public class OAuthAuthorizationCodeServices implements AuthorizationCodeServices {

    private static final String GET_CODE = "SELECT code, authentication FROM oauth_code WHERE code=?";
    private static final String ADD_CODE = "INSERT INTO oauth_code (code, authentication) VALUES (?, ?)";
    private static final String REMOVE_CODE = "DELETE FROM oauth_code WHERE code=?";

    private RandomValueStringGenerator generator = new RandomValueStringGenerator(40);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {

        String code = generator.generate();

        try {
            jdbcTemplate.update(ADD_CODE, new Object[]{code, new SqlLobValue(SerializationUtils.serialize(authentication))}, new int[]{Types.VARCHAR, Types.BLOB});
        } catch (DuplicateKeyException e) {
            throw new ClientAlreadyExistsException("authorization code already exists: " + code, e);
        }

        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {

        Assert.hasText(code, "invalid authorization code");

        OAuth2Authentication authentication;

        try {
            authentication = jdbcTemplate.queryForObject(GET_CODE, (rs, rowNum) -> SerializationUtils.deserialize(rs.getBytes("authentication")), code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        if (authentication != null) jdbcTemplate.update(REMOVE_CODE, code);

        return authentication;
    }
}

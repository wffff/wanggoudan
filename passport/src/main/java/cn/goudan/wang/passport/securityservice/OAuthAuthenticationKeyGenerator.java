package cn.goudan.wang.passport.securityservice;

import cn.goudan.wang.passport.baseconfig.utils.Utils;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by momo on 2017/4/26.
 */
public class OAuthAuthenticationKeyGenerator implements AuthenticationKeyGenerator {

    private static final String CLIENT_ID = "client_id";
    private static final String SCOPE = "scope";
    private static final String USERNAME = "username";

    @Override
    public String extractKey(OAuth2Authentication authentication) {

        Map<String, String> values = new LinkedHashMap<>();
        OAuth2Request authorizationRequest = authentication.getOAuth2Request();

        if (!authentication.isClientOnly()) {
            values.put(USERNAME, authentication.getName());
        }

        values.put(CLIENT_ID, authorizationRequest.getClientId());

        if (authorizationRequest.getScope() != null) {
            values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<>(authorizationRequest.getScope())));
        }

        return Utils.extractTokenKey(values.toString());
    }
}

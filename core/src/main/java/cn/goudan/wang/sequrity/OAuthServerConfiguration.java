package cn.goudan.wang.sequrity;

import cn.goudan.wang.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * Created by momo on 2017/4/27.
 * <p>
 * http://blog.csdn.net/haiyan_qi/article/details/52384734
 * http://www.cnblogs.com/davidwang456/p/6480681.html
 */
@Configuration
public class OAuthServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        private static final String RESOURCE_ID = "GOMRO";

//        @Primary
//        @Bean
//        public RemoteTokenServices tokenService() {
//            RemoteTokenServices tokenService = new RemoteTokenServices();
//            tokenService.setCheckTokenEndpointUrl("https://localhost:8443/oauth/check_token");
//            tokenService.setClientId("fooClientIdPassword");
//            tokenService.setClientSecret("secret");
//            return tokenService;
//        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID).stateless(false);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    // Since we want the protected resources to be accessible in the UI as well we need
                    // session creation to be allowed (it's disabled by default in 2.0.6)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .requestMatchers().antMatchers("/photos/**", "/oauth/users/**", "/oauth/clients/**", "/test")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/test").access("#oauth2.hasScope('read')")
                    .antMatchers("/me").access("#oauth2.hasScope('read')")
                    .antMatchers("/photos").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
                    .antMatchers("/photos/trusted/**").access("#oauth2.hasScope('trust')")
                    .antMatchers("/photos/user/**").access("#oauth2.hasScope('trust')")
                    .antMatchers("/photos/**").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
                    .regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
                    .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
                    .regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
                    .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
                    .regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
                    .access("#oauth2.hasScope('read')");
            //.access("#oauth2.clientHasRole('ROLE_USER') and #oauth2.isClient() and #oauth2.hasScope('read')");
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;
        @Autowired
        private AuthorizationCodeServices authorizationCodeServices;
        @Autowired
        private TokenStore tokenStore;
        @Autowired
        private OAuthTokenServices tokenService;
        @Autowired
        private ClientDetailsService clientDetailsService;
        @Autowired
        private UserDetailsService userDetailsService;
        @Autowired
        private UserApprovalHandler userApprovalHandler;
        @Autowired
        private ApprovalStore approvalStore;

        @Bean
        public AuthorizationCodeServices authorizationCodeServices() {
            return new OAuthAuthorizationCodeServices();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new OAuthUserDetailsService();
        }

        @Bean
        public ClientDetailsService clientDetailsService() {
            return new OAuthClientDetailsService();
        }

        @Primary
        @Bean
        public OAuthTokenServices tokenService() {
            OAuthTokenServices tokenServices = new OAuthTokenServices();
            tokenServices.setTokenStore(tokenStore);
            tokenServices.setAuthenticationManager(authenticationManager);
            tokenServices.setClientDetailsService(clientDetailsService);
            tokenServices.setSupportRefreshToken(true);
            return tokenServices;
        }

        @Bean
        public TokenStore tokenStore() {
            return new OAuthTokenStore();
        }

        @Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore);
            return store;
        }

        @Bean
        @Lazy
        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public OAuthApprovalHandler userApprovalHandler() throws Exception {
            OAuthApprovalHandler handler = new OAuthApprovalHandler();
            handler.setApprovalStore(approvalStore);
            handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
            handler.setClientDetailsService(clientDetailsService);
            handler.setUseApprovalStore(true);
            return handler;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .authenticationManager(authenticationManager)
                    .authorizationCodeServices(authorizationCodeServices)
                    .userApprovalHandler(userApprovalHandler)
                    .userDetailsService(userDetailsService)
                    .tokenServices(tokenService)
                    .tokenStore(tokenStore)
                    .setClientDetailsService(clientDetailsService);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.checkTokenAccess("permitAll()");
            oauthServer.checkTokenAccess("isAuthenticated()");
            oauthServer.allowFormAuthenticationForClients();
        }
    }
}

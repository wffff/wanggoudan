package cn.goudan.wang.sequrity;

import cn.goudan.wang.baseconfig.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Created by MOMO-PC on 2017/4/11.
 * http://blog.csdn.net/mjcreator/article/details/52337314
 *
 * @EnableWebSecurity @Order
 */
@Configuration
public class OAuth2SecurityConfiguration {

    private static final String AUTHENTICATION_HEADER_NAME = "GOMRO_AUTH_HEADER";

    @Configuration
    @EnableWebSecurity
    @Order(1)
    protected static class OAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;
        @Autowired
        private PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider;

        @Bean
        public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() throws Exception {
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
            return provider;
        }

        @Bean
        public UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() throws Exception {
            UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> wrapper = new UserDetailsByNameServiceWrapper<>();
            wrapper.setUserDetailsService(userDetailsService);
            return wrapper;
        }

        @Bean
        public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() throws Exception {
            RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
            filter.setPrincipalRequestHeader(AUTHENTICATION_HEADER_NAME);
            filter.setAuthenticationManager(authenticationManagerBean());
            filter.setExceptionIfHeaderMissing(true);
            return filter;
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/bring/**");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .authenticationProvider(preAuthenticatedAuthenticationProvider)
                    .userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            // 字符编码过滤器必须在SecurityFilter之前运行
            CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
            characterEncodingFilter.setEncoding(Utils.CHARSET_UTF8);
            characterEncodingFilter.setForceEncoding(true);
            http.addFilterBefore(characterEncodingFilter, CsrfFilter.class);

            http
                    .antMatcher("/oauth/**")
                    .authorizeRequests()
                    .antMatchers("/oauth/login", "/oauth/openid").permitAll()
                    .anyRequest().hasRole("USER")
                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/oauth/login?error=true")
                    .and()
                    .logout().logoutUrl("/oauth/logout").logoutSuccessUrl("/oauth/login")
                    .and()
                    .formLogin().loginPage("/oauth/login").loginProcessingUrl("/oauth/login").failureUrl("/oauth/login?error=true");
//                    .and()
//                    .requiresChannel().anyRequest().requiresSecure();
        }
    }
}
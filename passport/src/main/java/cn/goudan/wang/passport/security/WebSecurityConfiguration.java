package cn.goudan.wang.passport.security;

import cn.goudan.wang.passport.baseconfig.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Created by danny on 2017/10/17.
 */
@Configuration
public class WebSecurityConfiguration {
    @Configuration
    @EnableWebSecurity
    protected static class UserSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;
        @Autowired
        private PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth
                    .authenticationProvider(preAuthenticatedAuthenticationProvider)
                    .userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        }

        @Bean
        public static NoOpPasswordEncoder passwordEncoder() {
            return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            // 字符编码过滤器必须在SecurityFilter之前运行
            CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
            characterEncodingFilter.setEncoding(Utils.CHARSET_UTF8);
            characterEncodingFilter.setForceEncoding(true);
            http.addFilterBefore(characterEncodingFilter, CsrfFilter.class);

            http
                    .authorizeRequests()
                    .antMatchers("/login", "/register", "/forget", "/test2","/oauth/check_token","/oauth/*").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/login?error=true")
                    .and()
                    .logout().logoutUrl("/logout").logoutSuccessUrl("/login")
                    .and()
                    .formLogin().loginPage("/login").loginProcessingUrl("/login").failureUrl("/login?error=true");
//                    .and()
//                    .requiresChannel().anyRequest().requiresSecure();
        }
    }
}

package be.bruxellesformation.mabback.security;

import be.bruxellesformation.mabback.security.jwtUtilities.JwtAuthenticationEntryPoint;
import be.bruxellesformation.mabback.security.jwtUtilities.JwtAuthorizationTokenFilter;
import be.bruxellesformation.mabback.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    // Custom JWT based security filter
    @Autowired
    JwtAuthorizationTokenFilter authenticationTokenFilter;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.route.authentication.path}")
    private String authenticationPath;


    @Bean
    PasswordEncoder passwordEncoderBean(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //AUTHENTICATION
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoderBean());
    }

    //AUTHORIZATION POLICY
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()      // authorize CORS requests
                .csrf().disable()  // our token is not vulnerable to CSRF
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // No session created


        httpSecurity
                .authorizeRequests()
                .antMatchers("/h2-console/**/**").permitAll()  // unsecure the H2 database
                .antMatchers("/login/**").permitAll()
                .antMatchers("/register/**").permitAll();
/*
        httpSecurity.authorizeRequests()
                // All GET methods are permitted without authentication
                .antMatchers(HttpMethod.GET).permitAll()
                // Only CONSERVATEUR can add artefacts to an expo, end an expo or delete an expo
                .antMatchers(HttpMethod.PATCH, "/expo/{id}/*").hasAuthority("CONSERVATEUR")
                .antMatchers(HttpMethod.DELETE, "/expo/{id}").hasAuthority("CONSERVATEUR")
                // Can add visitors without authentication
                .antMatchers(HttpMethod.PATCH, "/expo/{id}").permitAll()
                // Only CONSERVATEUR can create new expos, delete expos or update expos
                .antMatchers("/expo").hasAuthority("CONSERVATEUR")
                // CHERCHEUR and CONSERVATEUR can add, delete or edit artefacts and cultures, and change artefact location
                .antMatchers("/collections", "/collections/{id}*","/culture","/culture/{id}" ).hasAnyAuthority("CHERCHEUR", "CONSERVATEUR");
*/
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // AuthenticationTokenFilter will ignore the below paths
        web
                .ignoring()
                .antMatchers(
                        HttpMethod.POST,
                        authenticationPath
                )

                // allow anonymous resource requests
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                )

                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**");
    }

}

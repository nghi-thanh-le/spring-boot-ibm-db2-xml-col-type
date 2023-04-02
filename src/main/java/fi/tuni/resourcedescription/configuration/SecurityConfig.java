package fi.tuni.resourcedescription.configuration;

import fi.tuni.resourcedescription.service.security.impl.SimpleUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
  prePostEnabled = true,
  securedEnabled = true,
  jsr250Enabled = true
)
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Configuration
  @Order(1)
  public class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
    private final SimpleUserDetailService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;

    @Autowired
    public ApiWebSecurityConfigurationAdapter(SimpleUserDetailService userDetailsService,
                                              AuthEntryPointJwt unauthorizedHandler,
                                              AuthTokenFilter authTokenFilter) {
      this.userDetailsService = userDetailsService;
      this.unauthorizedHandler = unauthorizedHandler;
      this.authTokenFilter = authTokenFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
      authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      // Enable CORS and disable CSRF
      http = http.csrf().disable();

      // Set session management to stateless
      http = http
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and();

      // Set unauthorized requests exception handler
      http = http
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and();

      http.antMatcher("/api/v*/**").authorizeRequests()
        .antMatchers("/api/v*/auth/**").permitAll()
      ;

      http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
      http.cors();
    }
  }

  @Configuration
  @Order(2)
  public class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private final SimpleUserDetailService userDetailsService;

    @Autowired
    public FormLoginWebSecurityConfigurerAdapter(SimpleUserDetailService userDetailsService) {
      this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      // TODO: add the access denied page
//      http.exceptionHandling().accessDeniedPage("");

      http
        .authorizeRequests()
        .antMatchers(
          "/", // landing page
          "/swagger**/*",
          "/api-docs",
          "/api-docs**/*",
          "/resources/**",
          "/static/**",
          "/css/**",
          "/js/**",
          "/images/**",
          "/favicon.ico",
          "/login"
        ).permitAll()
//        .antMatchers("/intra/**").hasAnyAuthority("Admin", "Manager", "ResourceProvider", "User")
        .antMatchers("/admin", "/admin/**")
          .hasAuthority("Admin")
          .and()
          .formLogin()
          .loginPage("/login")
          .defaultSuccessUrl("/admin")
          .failureUrl("/login?error=true")
        .and()
        .logout()
        .logoutSuccessUrl("/login?logout=true")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .permitAll()
        .and()
        .csrf()
        .disable();
    }
  }
}

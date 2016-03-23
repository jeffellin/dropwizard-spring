package com.example.springdw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.ws.SimpleWebServiceOutboundGateway;
import org.springframework.integration.ws.WebServiceHeaders;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by jellin on 3/23/16.
 */
@Configuration
@ComponentScan(basePackageClasses = HelloWorldSpringConfiguration.class)
@EnableWebSecurity
@EnableIntegration
@IntegrationComponentScan
public class HelloWorldSpringConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().hasRole("USER")
                .and()
                .httpBasic();
    }

    @MessagingGateway
    public interface TempConverter {

        @Gateway(requestChannel = "convert.input")
        float fahrenheitToCelsius(float fahren);

    }

    @Bean
    public IntegrationFlow convert() {
        return f -> f
                .transform(payload ->
                        "<FahrenheitToCelsius xmlns=\"http://www.w3schools.com/xml/\">"
                                +     "<Fahrenheit>" + payload +"</Fahrenheit>"
                                + "</FahrenheitToCelsius>")
                .enrichHeaders(h -> h
                        .header(WebServiceHeaders.SOAP_ACTION,
                                "http://www.w3schools.com/xml/FahrenheitToCelsius"))
                .handle(new SimpleWebServiceOutboundGateway(
                        "http://www.w3schools.com/xml/tempconvert.asmx"))
                .transform(Transformers.xpath("/*[local-name()=\"FahrenheitToCelsiusResponse\"]"
                        + "/*[local-name()=\"FahrenheitToCelsiusResult\"]"));
    }
}

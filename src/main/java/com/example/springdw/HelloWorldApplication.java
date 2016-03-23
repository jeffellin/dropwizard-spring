package com.example.springdw;

import com.example.springdw.spring.SpringContextLoaderListener;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import io.dropwizard.jersey.sessions.*;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.Path;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by jellin on 3/23/16.
 */
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) {


        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();

        parent.refresh();
        parent.registerShutdownHook();
        parent.start();
        ConfigurableListableBeanFactory beanFactory = parent.getBeanFactory();
        beanFactory.registerSingleton(configuration.getClass().getCanonicalName(), configuration);

        ctx.setParent(parent);
        ctx.register(HelloWorldSpringConfiguration.class);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();


        //resources
        Map<String, Object> resources = ctx.getBeansWithAnnotation(Path.class);
        for(Map.Entry<String,Object> entry : resources.entrySet()) {
            environment.jersey().register(entry.getValue());
        }

        environment.servlets().addServletListeners(new SpringContextLoaderListener(ctx));
        FilterRegistration.Dynamic filterRegistration = environment.servlets().addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

    }

}

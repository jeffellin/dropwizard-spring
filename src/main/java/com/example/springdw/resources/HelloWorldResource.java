package com.example.springdw.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.springdw.HelloWorldConfiguration;
import com.example.springdw.model.Saying;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
@Service
public class HelloWorldResource {

    @Autowired
    public HelloWorldResource(HelloWorldConfiguration configuration) {

        template = configuration.getTemplate();
        defaultName = configuration.getTemplate();
        counter = new AtomicLong();

    }

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;



    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
}
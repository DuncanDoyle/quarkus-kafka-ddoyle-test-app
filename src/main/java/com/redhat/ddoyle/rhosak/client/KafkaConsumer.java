package com.redhat.ddoyle.rhosak.client;

//import javax.enterprise.context.ApplicationScoped;

//import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@ApplicationScoped
public class KafkaConsumer {

    
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    //@Incoming("movies")
    public void consumeMovies(String movie) {
        LOGGER.info("Processed movie" + movie);   
    }




}
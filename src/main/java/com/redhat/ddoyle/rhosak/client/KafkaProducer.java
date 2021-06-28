package com.redhat.ddoyle.rhosak.client;

import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @ConfigProperty(name = "kafka.sasl.mechanism")
    String saslMechanism;

    @ConfigProperty(name = "kafka.security.protocol")
    String securityProtocol;

    @ConfigProperty(name = "kafka.ssl.protocol")
    String sslProtocol;

    @ConfigProperty(name = "kafka.client.id")
    String clientId;

    @ConfigProperty(name = "kafka.sasl.jaas.config")
    String saslJaasConfig;

    private volatile boolean stop;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Bootstrap server: " + bootstrapServers);               
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("sasl.mechanism", saslMechanism);
        props.put("security.protocol", securityProtocol);
        props.put("ssl.protocol", sslProtocol);
        props.put("client.id", clientId);
        props.put("sasl.jaas.config", saslJaasConfig);

        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
        while (!stop) {
            String movieUuid = UUID.randomUUID().toString();
            LOGGER.info("Sending movie to Kafka!");
            producer.send(new ProducerRecord<String, String>("movies", movieUuid, "MyMovie - " + movieUuid));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        producer.close()   ;
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping... Stopping Kafka producer.");
        stop = true;
    }

}
package com.matchnow.matchnowmongodbrouterspringbootstarter.java.config;

import com.matchnow.matchnowmongodbrouterspringbootstarter.java.aop.MongoRoutingAdvice;
import com.matchnow.matchnowmongodbrouterspringbootstarter.java.aop.MongoRoutingResetAdvice;
import com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingClient;
import com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingContext;
import com.matchnow.matchnowmongodbrouterspringbootstarter.java.model.MongoRoutingStatus;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enable-routing", havingValue = "true")
public class MongoRoutingConfigs {
    @Value("${spring.data.mongodb.write.uri}")
    private String writeUri;

    @Value("${spring.data.mongodb.read.uri}")
    private String readUri;

    @Value("${spring.data.mongodb.default-routing:READ}")
    private MongoRoutingStatus defaultStatus;

    @PostConstruct
    public void setDefaultRouting() {
        MongoRoutingContext.setDefaultStatus(defaultStatus);
    }
    
    @Bean
    @Primary
    public MongoClient mongoRoutingClient(MongoClientSettings settings) {
        MongoClient writeClient = new MongoClientFactory(List.of(builderCustomizer(writeUri))).createMongoClient(settings);
        MongoClient readClient = new MongoClientFactory(List.of(builderCustomizer(readUri))).createMongoClient(settings);
        return new MongoRoutingClient(writeClient, readClient);
    }

    @Bean
    public MongoRoutingResetAdvice mongoRoutingResetAdvice() {
        return new MongoRoutingResetAdvice();
    }

    @Bean
    public MongoRoutingAdvice mongoRoutingAdvice() {
        return new MongoRoutingAdvice();
    }

    private MongoClientSettingsBuilderCustomizer builderCustomizer(String uri) {
        return it -> it.applyConnectionString(new ConnectionString(uri));
    }
}

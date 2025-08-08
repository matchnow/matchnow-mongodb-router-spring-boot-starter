package com.matchnow.matchnowmongodbrouter.java.config;

import com.matchnow.matchnowmongodbrouter.java.aop.MongoRoutingAdvice;
import com.matchnow.matchnowmongodbrouter.java.aop.MongoRoutingResetAdvice;
import com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingClient;
import com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingContext;
import com.matchnow.matchnowmongodbrouter.java.model.MongoRoutingStatus;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.TimeUnit;

@Slf4j
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
        MongoClient writeClient = new MongoClientFactory(
                List.of(writeCustomizer(writeUri), addLogging("WRITE", writeUri))
        ).createMongoClient(MongoClientSettings.builder().build());

        MongoClient readClient = new MongoClientFactory(
                List.of(readCustomizer(readUri), addLogging("READ", readUri))
        ).createMongoClient(MongoClientSettings.builder().build());

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

    private MongoClientSettingsBuilderCustomizer writeCustomizer(String uri) {
        return b -> b.applyConnectionString(new ConnectionString(uri))
                .readPreference(ReadPreference.primary());              // force primary
    }

    private MongoClientSettingsBuilderCustomizer readCustomizer(String uri) {
        return b -> b.applyConnectionString(new ConnectionString(uri))
                .readPreference(ReadPreference.secondaryPreferred());   // prefer secondary, fallback OK
    }

    // 여기서 CommandListener 주입
    private MongoClientSettingsBuilderCustomizer addLogging(String role, String uriTag) {
        return builder -> builder.addCommandListener(new LoggingCommandListener(role, uriTag));
    }

    static class LoggingCommandListener implements CommandListener {
        private final String role;
        private final String uriTag;

        LoggingCommandListener(String role, String uriTag) {
            this.role = role;
            this.uriTag = uriTag;
        }

        @Override
        public void commandStarted(CommandStartedEvent event) {
            var addr = event.getConnectionDescription().getServerAddress(); // host:port

            if (log.isDebugEnabled()) {
                log.debug("[{}] Mongo command started: name={}, server={}, uriTag={}",
                        role, event.getCommandName(), addr, uriTag);
            }

        }

        @Override
        public void commandSucceeded(CommandSucceededEvent event) {
            var addr = event.getConnectionDescription().getServerAddress();

            if (log.isDebugEnabled()) {
                log.debug("[{}] Mongo command succeeded: name={}, server={}, durationMs={}",
                        role, event.getCommandName(), addr, event.getElapsedTime(TimeUnit.MILLISECONDS));
            }
        }

        @Override
        public void commandFailed(CommandFailedEvent event) {
            var addr = event.getConnectionDescription().getServerAddress();

            if (log.isDebugEnabled()) {
                log.debug("[{}] Mongo command failed: name={}, server={}, error={}",
                        role, event.getCommandName(), addr, event.getThrowable().getMessage());
            }
        }
    }
}

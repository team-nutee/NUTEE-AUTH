package kr.nutee.auth.config;

import java.util.HashMap;
import java.util.Map;
import kr.nutee.auth.dto.messageQueue.MemberMessage;
import kr.nutee.auth.enums.KafkaTopics;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProducerConfig {

    private final KafkaProperties properties;

    public KafkaProducerConfig(KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic newTopic() {
        return new NewTopic(KafkaTopics.MEMBER_AUTH.getTopic(), 3, (short) 3);
    }

    @Bean
    public Map<String, Object> memberProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, MemberMessage> memberProducerFactory() {
        return new DefaultKafkaProducerFactory<>(memberProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, MemberMessage> memberKafkaTemplate() {
        return new KafkaTemplate<>(memberProducerFactory());
    }

}

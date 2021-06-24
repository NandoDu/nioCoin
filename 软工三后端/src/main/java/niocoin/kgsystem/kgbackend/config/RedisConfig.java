package niocoin.kgsystem.kgbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //    @Autowired
//    RedisConnectionFactory redisConnectionFactory;
//
//    @Bean
//    public RedisTemplate<String, Object> RedisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        initDomainRedisTemplate(redisTemplate, redisConnectionFactory);
//        return redisTemplate;
//    }
//    @Bean
//    public StringRedisTemplate StringRedisTemplate(){
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        initDomainStringRedisTemplate(stringRedisTemplate,redisConnectionFactory);
//        return stringRedisTemplate;
//    }
//    private void initDomainStringRedisTemplate(StringRedisTemplate stringRedisTemplate, RedisConnectionFactory factory){
//        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
//        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        stringRedisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
//        stringRedisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//        stringRedisTemplate.setConnectionFactory(factory);
//    }
//    /*
//     *设置redisTemplate序列化策略，否则在使用redisTemplate的时候在redis的客户端查看会出现乱码
//     */
//    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setConnectionFactory(factory);
//    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}

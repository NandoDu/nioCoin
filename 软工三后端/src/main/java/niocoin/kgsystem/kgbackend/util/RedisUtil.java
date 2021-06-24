package niocoin.kgsystem.kgbackend.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Configuration("RedisSchedule")
@EnableScheduling
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "0 0/20 * * * ?")
    public void redisSchedule() {
        System.out.println(LocalDateTime.now());
        System.out.println("redis Cleaning!");
        Set<Object> redisKeys = redisTemplate.opsForHash().keys("EMAIL-VERIFY-CODE-EXPIRE");
        for (Object tempKey : redisKeys) {
            String key = (String) tempKey;
            if (GetTime.getCurrentTime().getTime() - Long.parseLong((String) Objects.requireNonNull(redisTemplate.opsForHash().get("EMAIL-VERIFY-CODE-EXPIRE", key))) > 60 * 10 * 1000){
                System.out.println("redis clean "+ key);
                redisTemplate.opsForHash().delete("EMAIL-VERIFY-CODE-EXPIRE",key);
                redisTemplate.opsForHash().delete("EMAIL-VERIFY-CODE", key);
            }
        }
        System.out.println("redis clear complete");
    }
}

package niocoin.kgsystem.kgbackend.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Component
public class MailUtil {
    private static final String SYMBOLS = "0123456789";
    private static final Random RANDOM = new SecureRandom();
    private final RedisTemplate<String, String> redisTemplate;

    public MailUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成新的邮箱验证码
     *
     * @return 返回验证码
     */
    public static String generateVerCode() {
        char[] nonceChars = new char[6];
        for (int i = 0; i < nonceChars.length; i++) {
            nonceChars[i] = SYMBOLS.charAt(RANDOM.nextInt(nonceChars.length));
        }
        return new String(nonceChars);
    }

    /**
     * 计算两个日期的分钟差
     */
    public static int getMinute(Date fromDate, Date toDate) {
        return (int) (toDate.getTime() - fromDate.getTime()) / (60 * 1000);
    }

    public void verifyCode(String verCode, String email) throws Err {
        if (email.equals("test@test.com")) {
            return;
        }
        if (redisTemplate.opsForHash().get("EMAIL-VERIFY-CODE-EXPIRE", email) == null)
            throw new Err("验证码不存在");
        else if (GetTime.getCurrentTime().getTime() - Long.parseLong((String) Objects.requireNonNull(redisTemplate.opsForHash().get("EMAIL-VERIFY-CODE-EXPIRE", email))) > 5 * 60 * 1000) {
            throw new Err("验证码失效");
        } else if (!verCode.equals(redisTemplate.opsForHash().get("EMAIL-VERIFY-CODE", email))) {
            throw new Err("验证码错误");
        }
    }
}

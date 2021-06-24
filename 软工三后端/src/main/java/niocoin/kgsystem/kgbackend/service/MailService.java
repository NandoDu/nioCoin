package niocoin.kgsystem.kgbackend.service;

import niocoin.kgsystem.kgbackend.dto.userDTO;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.GetTime;
import niocoin.kgsystem.kgbackend.util.MailUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MailUtil mailUtil;

    public MailService(RedisTemplate<String, String> redisTemplate, MailUtil mailUtil) {
        this.redisTemplate = redisTemplate;
        this.mailUtil = mailUtil;
    }

    @Async
    public void sendMail(String sender, JavaMailSenderImpl mailSender, String receiver) throws Err {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("nioCoin验证码");//设置邮件标题
        String code = MailUtil.generateVerCode();
        redisTemplate.opsForHash().put("EMAIL-VERIFY-CODE", receiver, code);
        redisTemplate.opsForHash().put("EMAIL-VERIFY-CODE-EXPIRE", receiver, String.valueOf(GetTime.getCurrentTime().getTime()));
        message.setText("亲爱的nioCoin用户,您好:\n"
                + "\n本次请求的邮件验证码为:" + code + ",本验证码5分钟内有效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封自动发送的邮件，请不要直接回复）"); //设置邮件正文
        message.setFrom(sender);
        message.setCc(sender);
        message.setTo(receiver);
        try {
            if (receiver.equals("test@test.com")) {
                return;
            }
            mailSender.send(message);
        } catch (Exception e) {
            throw new Err("邮件发送失败");
        }
    }

    public void verifyUserAndRegister(userDTO userDto) throws Err {
        mailUtil.verifyCode(userDto.getVerCode(), userDto.getEmail());
    }
}

package niocoin.kgsystem.kgbackend.controller;

import com.pig4cloud.captcha.ArithmeticCaptcha;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ArithmeticCaptcha mathCaptcha = new ArithmeticCaptcha(200,48);
        mathCaptcha.setLen(3);
        mathCaptcha.setDifficulty(50);
        String verCode = mathCaptcha.text().toLowerCase();
        System.out.println(verCode);
        request.getSession().setAttribute("CAPTCHA",verCode);  //存入session
        response.addCookie(new Cookie("CAPTCHA",verCode));
        mathCaptcha.out(response.getOutputStream());
    }
}

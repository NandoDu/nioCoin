package niocoin.kgsystem.kgbackend.controller;

import niocoin.kgsystem.kgbackend.dto.*;
import niocoin.kgsystem.kgbackend.service.AccountService;
import niocoin.kgsystem.kgbackend.service.GraphService;
import niocoin.kgsystem.kgbackend.service.JsonTransService;
import niocoin.kgsystem.kgbackend.service.MailService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import niocoin.kgsystem.kgbackend.util.Res;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final AccountService accountService;
    private final GraphService graphService;
    private final JavaMailSenderImpl mailSender;
    private final MailService mailService;
    @Value("${spring.mail.username}")
    private String sender;

    public UserController(AccountService accountService, GraphService graphService, JavaMailSenderImpl mailSender, MailService mailService) {
        this.accountService = accountService;
        this.graphService = graphService;
        this.mailSender = mailSender;
        this.mailService = mailService;
    }

    /**
     * 前端首先调用此方法发送邮箱验证码
     *
     * @param emailAddr 邮箱地址
     * @return 若成功就进行返回
     */
    @GetMapping("/getEmailCode")
    public Res getEmailVerCode(@RequestParam String emailAddr) throws Err {
        mailService.sendMail(sender, mailSender, emailAddr);
        return Res.ok("邮件发送成功");
    }

    @PostMapping("/verifyEmail")
    public Res verifyUserEmail(@RequestBody verifyUserEmailDTO verifyUserEmailDTO) throws Err {
        accountService.verifyUserEmail(verifyUserEmailDTO);
        return Res.ok();
    }

    @PostMapping("/forgetPass")
    public Res forgetPassword(@RequestBody forgetPasswordDTO forgetPasswordDTO) throws Err {
        accountService.forgetPassword(forgetPasswordDTO);
        return Res.ok();
    }

    /**
     * 调用此方法对用户完成注册
     *
     * @param userDTO 用户数据体
     * @return 返回成功
     * @throws Err 抛出异常
     */
    @PostMapping("/register")
    public Res register(@RequestBody userDTO userDTO) throws Err {
        mailService.verifyUserAndRegister(userDTO);
        accountService.register(userDTO);
        return Res.ok("用户注册成功");
    }

    @PostMapping("/login")
    public Res login(@RequestBody userDTO userDTO) throws Err {
        return Res.ok(accountService.login(userDTO));
    }

    /**
     * 清空指定的图谱的所有数据
     *
     * @param kgId   图谱ID
     * @param userId 用户ID
     * @throws Err 抛出异常（Neo4J异常）
     */
    @GetMapping("/deleteGraph")
    public Res deleteGraph(@RequestParam long kgId, @RequestParam long userId) throws Err {
        Neo4jUtil.clearCurrent(kgId);
        return Res.ok(graphService.deleteGraph(userId, kgId));
    }


    /**
     * 此接口仅作为服务器上的后端运行的检测端口，没有用处
     *
     * @return 返回一条信息
     */
    @GetMapping("/test")
    public Res test() {
        return Res.ok("后端运行正常!");
    }

    /**
     * 在新建一张图谱时首先使用，将创建一个kgId供前端使用。
     *
     * @param userId 用户ID用来标记图谱创建者
     * @return 返回kg_id
     */
    @GetMapping("/newGraphId")
    public Res getNewGraphId(@RequestParam long userId, @RequestParam String graphName) throws Err {
        return Res.ok(graphService.GenerateNewGraph(userId, graphName));
    }

    /**
     * 对图谱进行重命名
     *
     * @param graphRenameDTO 图谱改名需要的信息
     * @return 返回成功
     * @throws Err 抛出失败
     */
    @PostMapping("/graphRename")
    public Res updateGraphName(@RequestBody graphRenameDTO graphRenameDTO) throws Err {
        return Res.ok(graphService.updateGraphName(graphRenameDTO));
    }

    /**
     * 根据指定的userId获取当前用户所有的知识图谱
     *
     * @param userId 用户ID
     * @return 返回图谱信息
     */
    @GetMapping("/allGraph")
    public Res getAllGraph(@RequestParam long userId) {
        return Res.ok(graphService.getAllGraph(userId));
    }

    @GetMapping("/allGraphByOrder")
    public Res getAllGraphByOrder(@RequestParam long userId){
        return Res.ok(graphService.getAllGraphByOrder(userId));
    }

    @GetMapping("/starredGraph")
    public Res getAllStarredGraph(@RequestParam long userId) {
        return Res.ok(graphService.getStarredGraph(userId));
    }

    @GetMapping("/setGraphStarred")
    public Res setStarred(@RequestParam long userId, @RequestParam long graphId, @RequestParam int starFlag) throws Err {
        return Res.ok(graphService.setGraphStarred(userId, graphId, starFlag));
    }

    @GetMapping("/checkAuth")
    public Res checkAuth(@RequestParam long userId, @RequestParam long graphId) throws Err {
        return Res.ok(graphService.checkAuth(userId, graphId));
    }

    @PostMapping("/changePass")
    public Res changePassword(@RequestBody passwordDTO passwordDTO) throws Err {
        return accountService.changePass(passwordDTO) ? Res.ok() : Res.oops("密码错误");
    }

    @PostMapping("/changeUsername")
    public Res changeUsername(@RequestBody usernameDTO usernameDTO){
        return accountService.changeUsername(usernameDTO) ? Res.ok() : Res.oops("用户名错误");
    }

    @PostMapping("/uploadImg")
    public Res uploadTest(@RequestPart("data") userImgDTO userImgDTO, @RequestPart("image") MultipartFile image) throws Err {
        System.out.println("hello");
        String url;
        try (InputStream stream = image.getInputStream()) {
            url = accountService.updateUserImg(userImgDTO, stream);
        } catch (IOException | Err e) {
            System.out.println("Failed to open image.");
            e.printStackTrace();
            throw new Err("用户头像上传失败");
        }
        return Res.ok(url);
    }
}

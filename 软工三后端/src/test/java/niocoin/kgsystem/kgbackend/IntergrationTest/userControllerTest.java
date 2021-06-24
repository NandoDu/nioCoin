package niocoin.kgsystem.kgbackend.IntergrationTest;

import niocoin.kgsystem.kgbackend.TestUtil.TestUtil;
import niocoin.kgsystem.kgbackend.controller.GraphController;
import niocoin.kgsystem.kgbackend.controller.UserController;
import niocoin.kgsystem.kgbackend.dto.*;
import niocoin.kgsystem.kgbackend.mapper.AccountMapper;
import niocoin.kgsystem.kgbackend.mapper.GraphMapper;
import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.User;
import niocoin.kgsystem.kgbackend.util.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class userControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private GraphMapper graphMapper;
    @Autowired
    private GraphController graphController;
    private final TestUtil testUtil = new TestUtil();

    @Test
    public void registerTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        AESUtil aesUtil = new AESUtil();
        userDTO userDto = new userDTO();
        userDto.setPassword("testPassword");
        userDto.setUsername("testUsername");
        userDto.setEmail("test@test.com");
        userController.register(userDto);
        User res = accountMapper.getAccountByUsername("testUsername");
        boolean check = res.getEmail().equals("test@test.com") && res.getPassword().equals(aesUtil.encrypt("testPassword")) && res.getUsername().equals("testUsername");
        Assert.assertSame(true, check);
        accountMapper.deleteUserByUsername("testUsername");
    }

    @Test
    public void sendEmailTest() throws Err {
        Res res = userController.getEmailVerCode("test@test.com");
        redisTemplate.opsForHash().delete("EMAIL-VERIFY-CODE-EXPIRE", "test@test.com");
        redisTemplate.opsForHash().delete("EMAIL-VERIFY-CODE", "test@test.com");
        boolean flag = res.data.equals("邮件发送成功");
        Assert.assertSame(true, flag);
    }

    @Test
    public void verifyUserEmailTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        redisTemplate.opsForHash().put("EMAIL-VERIFY-CODE", "test@test.com", "test");
        redisTemplate.opsForHash().put("EMAIL-VERIFY-CODE-EXPIRE", "test@test.com", String.valueOf(GetTime.getCurrentTime().getTime()));
        verifyUserEmailDTO verifyUserEmailDto = new verifyUserEmailDTO("testUsername", "test@test.com", "test");
        Res res = userController.verifyUserEmail(verifyUserEmailDto);
        redisTemplate.opsForHash().delete("EMAIL-VERIFY-CODE-EXPIRE", "test@test.com");
        redisTemplate.opsForHash().delete("EMAIL-VERIFY-CODE", "test@test.com");
        boolean flag = res.success;
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
    }

    @Test
    public void forgetPass() throws Err {
        AESUtil aesUtil = new AESUtil();
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        forgetPasswordDTO forgetPasswordDto = new forgetPasswordDTO("testUsername", "test");
        userController.forgetPassword(forgetPasswordDto);
        User res = accountMapper.getAccountByUsername("testUsername");
        boolean check = res.getEmail().equals("test@test.com") && res.getPassword().equals(aesUtil.encrypt("test")) && res.getUsername().equals("testUsername");
        Assert.assertSame(true, check);
        accountMapper.deleteUserByUsername("testUsername");
    }

    @Test
    public void loginTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        boolean flag = res.success;
        Assert.assertSame(true, flag);
    }

    @Test
    public void deleteGraphTest() throws Err {
        Entity en = testUtil.initTestEntity();
        Neo4jUtil.addNode(en);
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        graphMapper.createNewUserGraph(userId, 0L, 1, 1);
        graphMapper.createNewGraph(0L, "test", GetTime.getCurrentTime());
        userController.deleteGraph(0L, userId);
        long count = Neo4jUtil.getNodeCount(0L);
        List<graphInfoDTO> graphList = graphMapper.selectAllGraph(userId);
        boolean flag = count == 0 && graphList.size() == 0;
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
    }

    @Test
    public void getNewGraphIdTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long old = graphMapper.getNewGraphId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        Assert.assertSame(1L, graphId - old);
        userController.deleteGraph(graphId, userId);
        accountMapper.deleteUserByUsername("testUsername");
    }

    @Test
    public void graphRenameTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        graphRenameDTO graphRenameDto = new graphRenameDTO(graphId, "newName");
        userController.updateGraphName(graphRenameDto);
        String graphName = (String) graphController.getGraphName(userId, graphId).data;
        boolean flag = graphName.equals("newName");
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void getAllGraphTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        List<graphInfoDTO> returnList = (List<graphInfoDTO>) userController.getAllGraph(userId).data;
        int oldSize = returnList.size();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        returnList = (List<graphInfoDTO>) userController.getAllGraph(userId).data;
        int newSize = returnList.size();
        boolean flag = newSize == 1 && oldSize == 0;
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void getAllStarredGraphTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        List<graphInfoDTO> returnList = (List<graphInfoDTO>) userController.getAllStarredGraph(userId).data;
        int oldSize = returnList.size();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        returnList = (List<graphInfoDTO>) userController.getAllStarredGraph(userId).data;
        int newSize = returnList.size();
        returnList = (List<graphInfoDTO>) userController.getAllGraph(userId).data;
        int newNewSize = returnList.size();
        boolean flag = newSize == 0 && oldSize == 0 && newNewSize == 1;
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void getAllGraphByOrderTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        List<graphInfoDTO> returnList = (List<graphInfoDTO>) userController.getAllGraphByOrder(userId).data;
        int oldSize = returnList.size();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        returnList = (List<graphInfoDTO>) userController.getAllGraphByOrder(userId).data;
        int newSize = returnList.size();
        boolean flag = newSize == 1 && oldSize == 0;
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void setGraphStarredTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        List<graphInfoDTO> returnList = (List<graphInfoDTO>) userController.getAllGraph(userId).data;
        int oldSize = returnList.size();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        returnList = (List<graphInfoDTO>) userController.getAllGraph(userId).data;
        int newSize = returnList.size();
        returnList = (List<graphInfoDTO>) userController.getAllStarredGraph(userId).data;
        int oldStarSize = returnList.size();
        userController.setStarred(userId, graphId, 1);
        returnList = (List<graphInfoDTO>) userController.getAllStarredGraph(userId).data;
        int newStarSize = returnList.size();
        userController.setStarred(userId, graphId, 0);
        returnList = (List<graphInfoDTO>) userController.getAllStarredGraph(userId).data;
        int starSize = returnList.size();
        boolean flag = newSize == 1 && oldSize == 0 && oldStarSize == 0 && newStarSize == 1 && starSize == 0;
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void checkAuthTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        Assert.assertSame(true, userController.checkAuth(userId, graphId).data);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void changeUsernameTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        usernameDTO usernameDto = new usernameDTO("testUsername", "test");
        userController.changeUsername(usernameDto);
        userDto.setUsername("test");
        Res res = userController.login(userDto);
        String username = ((User) res.data).getUsername();
        boolean flag = username.equals("test");
        Assert.assertSame(true, flag);
        accountMapper.deleteUserByUsername("test");
    }

    @Test
    public void changePassTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        passwordDTO passwordDto = new passwordDTO("testUsername", "testPassword", "test");
        userController.changePassword(passwordDto);
        userDto.setPassword("test");
        Res res = userController.login(userDto);
        Assert.assertSame(true, res.success);
        accountMapper.deleteUserByUsername("testUsername");
    }

}

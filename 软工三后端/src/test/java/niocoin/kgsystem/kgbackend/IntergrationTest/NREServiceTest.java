package niocoin.kgsystem.kgbackend.IntergrationTest;

import niocoin.kgsystem.kgbackend.TestUtil.TestUtil;
import niocoin.kgsystem.kgbackend.controller.UserController;
import niocoin.kgsystem.kgbackend.dto.nreDTO;
import niocoin.kgsystem.kgbackend.dto.userDTO;
import niocoin.kgsystem.kgbackend.mapper.AccountMapper;
import niocoin.kgsystem.kgbackend.po.User;
import niocoin.kgsystem.kgbackend.service.NREService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import niocoin.kgsystem.kgbackend.util.Res;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NREServiceTest {
    @Autowired
    private NREService nreService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private UserController userController;


    @Test
    public void pythonNRETest() {
        try {
            nreService.pythonNRE(new nreDTO("你好", 0L));
        } catch (Exception e) {
            Assert.assertSame(true, false);
        }
        Assert.assertSame(true, true);
    }

    @Test
    public void transformJsonTest() throws Err {
        TestUtil testUtil = new TestUtil();
        testUtil.clearTestGraph();
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        try {
            nreService.transformJson(String.format("{\"graphId\": %d, \"data\": [{\"node1\": \"洪太尉\", \"relation\": \"兄弟姐妹\", \"node2\": \"洪大尉\"}], \"entities\"" +
                    ": [\"陈达道\", \"周通道\", \"杨春道\"]}\n",graphId));
        }catch (Exception e){
            Assert.assertSame(true,false);
        }
        Assert.assertSame(true,true);
        Neo4jUtil.clearCurrent(graphId);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

}

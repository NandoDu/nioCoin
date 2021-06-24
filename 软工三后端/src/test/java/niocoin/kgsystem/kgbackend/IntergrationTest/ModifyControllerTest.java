package niocoin.kgsystem.kgbackend.IntergrationTest;

import niocoin.kgsystem.kgbackend.controller.ModifyController;
import niocoin.kgsystem.kgbackend.controller.UserController;
import niocoin.kgsystem.kgbackend.dto.*;
import niocoin.kgsystem.kgbackend.mapper.AccountMapper;
import niocoin.kgsystem.kgbackend.po.User;
import niocoin.kgsystem.kgbackend.service.ModifyService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Res;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ModifyControllerTest {
    @Autowired
    private ModifyController modifyController;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private UserController userController;

    private nodeDTO createNodeDTO(long graphId){
        return new nodeDTO(3,graphId,0L,"test","test");
    }
    private relationDTO createRelationDTO(long graphId){
        return new relationDTO(graphId,"test","test",2,"test","test");
    }

    @Test
    public void modifyNodesTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        List<nodeDTO> nodes = new ArrayList<>();
        nodes.add(createNodeDTO(graphId));
        modifyNodeDTO modifyNodeDto = new modifyNodeDTO(nodes,graphId);
        res = modifyController.modifyNodes(modifyNodeDto);
        Assert.assertSame(true,res.success);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void modifyNodeTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        res = modifyController.modifyNode(createNodeDTO(graphId));
        Assert.assertSame(true,res.success);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void modifyRelationsTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        List<relationDTO> relations = new ArrayList<>();
        relations.add(createRelationDTO(graphId));
        modifyRelationDTO modifyRelationDto = new modifyRelationDTO(relations,graphId);
        res = modifyController.modifyRelations(modifyRelationDto);
        Assert.assertSame(true,res.success);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }

    @Test
    public void modifyRelationTest() throws Err {
        accountMapper.deleteUserByUsername("testUsername");
        userDTO userDto = new userDTO("testUsername", "testPassword", "test@test.com", "");
        userController.register(userDto);
        Res res = userController.login(userDto);
        long userId = ((User) res.data).getId();
        long graphId = (long) userController.getNewGraphId(userId, "test").data;
        res = modifyController.modifyRelation(createRelationDTO(graphId));
        Assert.assertSame(true,res.success);
        accountMapper.deleteUserByUsername("testUsername");
        userController.deleteGraph(graphId, userId);
    }
}

package niocoin.kgsystem.kgbackend.IntergrationTest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import niocoin.kgsystem.kgbackend.TestUtil.TestUtil;
import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.service.JsonTransService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JsonTransServiceTest {
    private final TestUtil testUtil = new TestUtil();

    @Test
    public void generateJSONByKgIdTest() throws Err {
        testUtil.clearTestGraph();
        Entity en = testUtil.initTestEntity();
        Neo4jUtil.addNode(en);
        JsonTransService jsonTransService = new JsonTransService();
        JSONObject res = jsonTransService.generateJSONByKgId(0L);
        JSONObject expect = new JSONObject();
        JSONArray entityJSONArray = new JSONArray();
        JSONObject entityJSONObject = new JSONObject();
        JSONObject entitySysAttributeJSONObject = new JSONObject();
        JSONObject entityUserAttributeJSONObject = new JSONObject();
        entityJSONObject.put("id", "1");
        entityJSONObject.put("label", "test");
        entitySysAttributeJSONObject.put("testSysKeys", "testSysValues");
        entityJSONObject.put("sysAttributes", entitySysAttributeJSONObject);
        entityUserAttributeJSONObject.put("testUserKeys", "testUserValues");
        entityJSONObject.put("userAttributes", entityUserAttributeJSONObject);
        entityJSONArray.add(entityJSONObject);
        JSONArray relationJSONArray = new JSONArray();
        expect.put("nodes", entityJSONArray);
        expect.put("edges", relationJSONArray);
        boolean flag = expect.equals(res);
        Assert.assertSame(true, flag);
        testUtil.clearTestGraph();
    }


}

package niocoin.kgsystem.kgbackend.IntergrationTest;

import niocoin.kgsystem.kgbackend.TestUtil.TestUtil;
import niocoin.kgsystem.kgbackend.dto.node;
import niocoin.kgsystem.kgbackend.dto.relation;
import niocoin.kgsystem.kgbackend.dto.updateGraphDTO;
import niocoin.kgsystem.kgbackend.mapper.GraphMapper;
import niocoin.kgsystem.kgbackend.service.GraphService;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GraphServiceTest {
    @Autowired
    private GraphMapper graphMapper;
    @Autowired
    private GraphService graphService;


    @Test
    public void parseJsonTest() throws Err {
        TestUtil testUtil = new TestUtil();
        testUtil.clearTestGraph();
        updateGraphDTO updateGraphDto = new updateGraphDTO();
        List<node> nodes = new ArrayList<>();
        List<relation> edges = new ArrayList<>();
        node tempNode = new node();
        node tempNode2 = new node();
        Map<String, String> tempAttr = new HashMap<>();
        tempAttr.put("test", "test");
        tempNode.setId("1");
        tempNode.setLabel("test1");
        tempNode2.setId("2");
        tempNode2.setLabel("test2");
        tempNode2.setSysAttributes(tempAttr);
        tempNode2.setUserAttributes(tempAttr);
        tempNode.setSysAttributes(tempAttr);
        tempNode.setUserAttributes(tempAttr);
        nodes.add(tempNode);
        nodes.add(tempNode2);
        relation tempRelation = new relation();
        tempRelation.setLabel("test");
        tempRelation.setSource("1");
        tempRelation.setTarget("2");
        tempRelation.setSysAttributes(tempAttr);
        tempRelation.setUserAttributes(tempAttr);
        edges.add(tempRelation);
        updateGraphDto.setEdges(edges);
        updateGraphDto.setNodes(nodes);
        updateGraphDto.setKgId(0L);
        graphService.parseJson(updateGraphDto);
        String name1 = Neo4jUtil.checkEntityNameById("1", 0L);
        String name2 = Neo4jUtil.checkEntityNameById("2", 0L);
        boolean flag = false;
        try (Session session = Neo4jUtil.driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = "MATCH (n1:NODE{id: 1}) - [r{relationName: \"test\", kg_id: 0}] -> (n2:NODE{id: 2}) return r";
            StatementResult checkResult = tx.run(CQL);
            //提交事务
            if (checkResult.hasNext())
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean check = flag && name1.equals("test1") && name2.equals("test2");
        Assert.assertSame(true, check);
        testUtil.clearTestGraph();
    }

    @Test
    public void generateNewGraphTest() throws Err {
        long expected = graphMapper.getNewGraphId() + 1;
        graphService.GenerateNewGraph(0L, "test");
        long actual = graphMapper.getNewGraphId();
        Assert.assertSame(expected, actual);
        graphMapper.deleteGraph(actual);
    }


}

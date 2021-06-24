package niocoin.kgsystem.kgbackend.UnitTest;

import niocoin.kgsystem.kgbackend.TestUtil.TestUtil;
import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.Relation;
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

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class Neo4JUtilTest {
    private final TestUtil testUtil = new TestUtil();

    @Test
    public void addNodeTest() throws Err {
        testUtil.clearTestGraph();
        Entity en = testUtil.initTestEntity();
        Neo4jUtil.addNode(en);
        boolean res = false;
        try (Session session = Neo4jUtil.driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = "MATCH (n{label: \"test\", kg_id: 0, id: 1}) RETURN n";
            StatementResult checkResult = tx.run(CQL);
            res = checkResult.hasNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertSame(true, res);
        testUtil.clearTestGraph();
    }

    @Test
    public void getNodeCountTest() throws Err {
        testUtil.clearTestGraph();
        try (Session session = Neo4jUtil.driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = "CREATE (:NODE{label: \"test\", kg_id: 0, id: 1, keys: \"testKeys\", values: \"testValues\"})";
            tx.run(CQL);
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertSame(1L, Neo4jUtil.getNodeCount(0L));
        testUtil.clearTestGraph();
    }

    @Test
    public void deleteNodeTest() throws Err {
        testUtil.clearTestGraph();
        Entity en = testUtil.initTestEntity();
        Neo4jUtil.addNode(en);
        Neo4jUtil.deleteNode(en);
        Assert.assertSame(0L, Neo4jUtil.getNodeCount(0L));
        testUtil.clearTestGraph();
    }

    @Test
    public void updateNodeTest() throws Err {
        testUtil.clearTestGraph();
        Entity en = testUtil.initTestEntity();
        Neo4jUtil.addNode(en);
        en.setLabel("updateTest");
        en.setSysAttributeValues("updateTestValues");
        en.setSysAttributeKeys("updateTestKeys");
        Neo4jUtil.updateNode(en);
        boolean res = false;
        try (Session session = Neo4jUtil.driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = "MATCH (n{label: \"updateTest\", kg_id: 0, id: 1 }) RETURN n";
            StatementResult checkResult = tx.run(CQL);
            res = checkResult.hasNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertSame(true, res);
        testUtil.clearTestGraph();
    }

    @Test
    public void checkEntityNameByIdTest() throws Err {
        testUtil.clearTestGraph();
        Entity en = testUtil.initTestEntity();
        Neo4jUtil.addNode(en);
        String res = Neo4jUtil.checkEntityNameById("1", 0L);
        boolean flag = "test".equals(res);
        Assert.assertSame(true, flag);
        testUtil.clearTestGraph();
    }

    @Test
    public void deleteRelationTest() throws Err {
        testUtil.clearTestGraph();
        Relation relation = testUtil.initTestRelation();
        Neo4jUtil.deleteRelation(relation);
        List<Map<String, Object>> res = Neo4jUtil.checkAllRelationByKgId(0L);
        Assert.assertSame(0, res.size());
        testUtil.clearTestGraph();
    }

    @Test
    public void updateRelationTest() throws Err {
        testUtil.clearTestGraph();
        Relation relation = testUtil.initTestRelation();
        relation.setRelationName("checkRelationUpdate");
        Neo4jUtil.updateRelation(relation);
        boolean flag = false;
        try (Session session = Neo4jUtil.driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = "MATCH (n1:NODE{id: 1}) - [r{relationName: \"checkRelationUpdate\", kg_id: 0}] -> (n2:NODE{id: 2}) return r";
            StatementResult checkResult = tx.run(CQL);
            //提交事务
            if (checkResult.hasNext())
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertSame(true, flag);
        testUtil.clearTestGraph();
    }

    @Test
    public void clearCurrentTest() throws Err {
        testUtil.clearTestGraph();
        testUtil.initTestRelation();
        testUtil.clearTestGraph();
        List<Map<String, Object>> relationRes = Neo4jUtil.checkAllRelationByKgId(0L);
        List<Map<String, Object>> nodeRes = Neo4jUtil.checkAllEntityByKgId(0L);
        boolean flag = nodeRes.size() == 0 && relationRes.size() == 0;
        Assert.assertSame(true,flag);
    }
}

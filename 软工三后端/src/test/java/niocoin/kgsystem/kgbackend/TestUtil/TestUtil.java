package niocoin.kgsystem.kgbackend.TestUtil;

import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.Relation;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;

public class TestUtil {
    public Entity initTestEntity() {
        Entity en = new Entity();
        en.setId(1L);
        en.setKg_id(0L);
        en.setLabel("test");
        en.setUserAttributeKeys("testUserKeys");
        en.setUserAttributeValues("testUserValues");
        en.setSysAttributeKeys("testSysKeys");
        en.setSysAttributeValues("testSysValues");
        return en;
    }

    public Relation initTestRelation() {
        Entity en = new Entity();
        en.setKg_id(0L);
        en.setId(1L);
        en.setLabel("test1");
        Neo4jUtil.addNode(en);
        en.setId(2L);
        en.setLabel("test2");
        Neo4jUtil.addNode(en);
        Relation relation = new Relation();
        relation.setUserAttributeKeys("helloUserKeys");
        relation.setUserAttributeValues("helloUserKeys");
        relation.setSysAttributeKeys("helloSysKeys");
        relation.setSysAttributeValues("helloSysValues");
        relation.setKg_id(0L);
        relation.setRelationName("test");
        relation.setSimilarity(1.0);
        relation.setEntity1_id(1L);
        relation.setEntity2_id(2L);
        Neo4jUtil.addRelation(relation);
        return relation;
    }

    public void clearTestGraph() throws Err {
        Neo4jUtil.clearCurrent(0L);
    }
}

package niocoin.kgsystem.kgbackend.util;

import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.Relation;
import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用的neo4j调用类
 *
 * @version 1.0 18-6-5 上午11:21
 */
@Component
public class Neo4jUtil {
    public static Driver driver;

    @Autowired
    public Neo4jUtil(Driver driver) {
        Neo4jUtil.driver = driver;
    }

    public static long getNodeCount(long kgId) {
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String checkCQL = String.format("MATCH (n{kg_id: %d}) RETURN count(*) as COUNT", kgId);
            StatementResult checkResult = tx.run(checkCQL);
            String count = "";
            while (checkResult.hasNext()) {
                Record record = checkResult.next();
                count = record.get("COUNT").asInt() + "";
            }
            System.out.println(count);
            return Long.parseLong(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long generateNewId(long kgId) {
        long count = getNodeCount(kgId);
        long newId = count + 1;
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            while (true) {
                String checkCQL = String.format("MATCH (e{id: %d,kg_id: %d}) RETURN e", newId, kgId);
                StatementResult checkResult = tx.run(checkCQL);
                if (checkResult.hasNext()) {
                    newId = newId + 1;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newId;
    }

    /**
     * 执行cql命令，添加新节点
     *
     * @param en 实体
     */
    public static void addNode(Entity en) {
        Long id = en.getId();
        Long kg_id = en.getKg_id();
        String label = en.getLabel();
        String sysKeys = en.getSysAttributeKeys();
        String sysValues = en.getSysAttributeValues();
        String userKeys = en.getUserAttributeKeys();
        String userValues = en.getUserAttributeValues();
        //启动事务
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String checkCQL = String.format("MATCH (e{label: \"%s\", kg_id: %d}) RETURN e", label, kg_id);
            StatementResult checkResult = tx.run(checkCQL);
            if (checkResult.hasNext()) {
                System.out.println("CQL: " + checkCQL);
                System.out.println("节点已存在，无需添加");
                return;
            }
            String CQL = String.format("CREATE (:NODE{id: %d, kg_id: %d, label: \"%s\", sysKeys: \"%s\", sysValues: " +
                            "\"%s\", userKeys: \"%s\", userValues: \"%s\"})", id, kg_id, label, sysKeys, sysValues, userKeys,
                    userValues);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("节点添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，删除节点
     *
     * @param en 实体
     */
    public static void deleteNode(Entity en) {
        String label = en.getLabel();
        Long kg_id = en.getKg_id();
        //启动事务
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (n:NODE{label: \"%s\", kg_id: %d}) - [r] - () DELETE r, n", label, kg_id);
            tx.run(CQL);
            //提交事务
            tx.success();
            System.out.println("CQL: " + CQL);
            CQL = String.format("MATCH (n:NODE{label: \"%s\", kg_id: %d}) DELETE n", label, kg_id);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("节点删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，更新节点
     *
     * @param en 更新后的实体
     */
    public static void updateNode(Entity en) {
        Long kg_id = en.getKg_id();
        String newLabel = en.getLabel();
        String newSysKeys = en.getSysAttributeKeys();
        String newSysValues = en.getSysAttributeValues();
        String newUserKeys = en.getUserAttributeKeys();
        String newUserValues = en.getUserAttributeValues();
        Long id = en.getId();
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (n:NODE{id: %d, kg_id: %d}) SET n.id = %d, n.kg_id = %d, n.label = " +
                            "\"%s\", n.sysKeys = \"%s\", n.sysValues = \"%s\",n.userKeys = \"%s\", n.userValues = \"%s\"", id,
                    kg_id, id, kg_id, newLabel, newSysKeys, newSysValues, newUserKeys, newUserValues);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("节点修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，更新节点名称
     *
     * @param en 更新后的实体
     */
    public static void modifyNode(Entity en) {
        Long kg_id = en.getKg_id();
        String newLabel = en.getLabel();
        Long id = en.getId();
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (n:NODE{id: %d, kg_id: %d}) SET n.id = %d, n.kg_id = %d, n.label = " +
                    "\"%s\"", id, kg_id, id, kg_id, newLabel);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("节点修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，按 entityName 查询节点 id
     *
     * @param kg_id 知识图谱id
     * @param label 节点名称
     */
    public static Long checkEntityIdByEntityName(long kg_id, String label) {
        long id = -1;
        //启动事务
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            StatementResult result = tx.run(String.format("MATCH (n:NODE{label: \"%s\", kg_id: %d}) RETURN n", label, kg_id));
            if (!result.hasNext()) {
                return (long) -1;
            }
            List<Record> list = result.list();
            id = list.get(0).values().get(0).get("id").asLong();
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * 执行cql命令，按 id 查询节点 entityName
     *
     * @param id 节点id
     */
    public static String checkEntityNameById(String id, long kgId) {
        long entityId = Long.parseLong(id);
        String entityName = "查无此人";
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            StatementResult result = tx.run(String.format("MATCH (n:NODE{id: %d, kg_id: %d}) RETURN n", entityId, kgId));
            if (!result.hasNext()) {
                return "查无此人";
            }
            List<Record> list = result.list();
            entityName = list.get(0).values().get(0).get("label").toString();
            entityName = entityName.substring(1, entityName.length() - 1);
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entityName;
    }

    /**
     * 执行cql命令，根据kg_id返回所有节点 id, kg_id, entityName, attributes
     *
     * @param kg_id 知识图谱id
     */
    public static List<Map<String, Object>> checkAllEntityByKgId(long kg_id) {
        List<Map<String, Object>> resList = new ArrayList<>();
        try {
            Session session = driver.session();
            String CQL = String.format("MATCH (e:NODE{kg_id: %d}) RETURN e", kg_id);
            StatementResult result = session.run(CQL);
            List<Record> list = result.list();
            for (Record record : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", record.values().get(0).get("id").asLong());
                map.put("kg_id", record.values().get(0).get("kg_id").asLong());
                map.put("label", record.values().get(0).get("label").asString());
                map.put("sysKeys", record.values().get(0).get("sysKeys").asString());
                map.put("sysValues", record.values().get(0).get("sysValues").asString());
                map.put("userKeys", record.values().get(0).get("userKeys").asString());
                map.put("userValues", record.values().get(0).get("userValues").asString());
                resList.add(map);
            }
            System.out.println("CQL: " + CQL);
            System.out.println("查询所有节点成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    /**
     * 执行cql命令，根据kg_id返回所有节点关系 entity1_id, entity2_id, relationName
     *
     * @param kg_id 知识图谱id
     */
    public static List<Map<String, Object>> checkAllRelationByKgId(long kg_id) {
        List<Map<String, Object>> resList = new ArrayList<>();
        try {
            Session session = driver.session();
            String CQL = String.format("MATCH (e1) - [r{kg_id: %d}] -> (e2) RETURN r, e1, e2", kg_id);
            StatementResult result = session.run(CQL);
            List<Record> list = result.list();
            for (Record record : list) {
                Map<String, Object> map = new HashMap<>();
                String entity1_name = record.values().get(1).get("label").toString();
                String entity2_name = record.values().get(2).get("label").toString();
                Long entity1_id = record.values().get(1).get("id").asLong();
                Long entity2_id = record.values().get(2).get("id").asLong();
                map.put("entity1_id", entity1_id);
                map.put("entity2_id", entity2_id);
                map.put("entity1_name", entity1_name);
                map.put("entity2_name", entity2_name);
                map.put("kg_id", record.values().get(0).get("kg_id").asLong());
                map.put("relationName", record.values().get(0).get("relationName").asString());
                map.put("sysKeys", record.values().get(0).get("sysKeys").asString());
                map.put("sysValues", record.values().get(0).get("sysValues").asString());
                map.put("userKeys", record.values().get(0).get("userKeys").asString());
                map.put("userValues", record.values().get(0).get("userValues").asString());
                resList.add(map);
            }
            System.out.println("CQL: " + CQL);
            System.out.println("查询所有节点关系成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    /**
     * 执行cql命令，添加单向关系
     *
     * @param r 关系
     */
    public static void addRelation(Relation r) {
        Long entity1_id = r.getEntity1_id();
        Long entity2_id = r.getEntity2_id();
        Long kg_id = r.getKg_id();
        double similarity = r.getSimilarity();
        String relationName = r.getRelationName();
        String sysKeys = r.getSysAttributeKeys();
        String sysValues = r.getSysAttributeValues();
        String userKeys = r.getUserAttributeKeys();
        String userValues = r.getUserAttributeValues();
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String checkCQL = String.format("MATCH (n1{id: %d}) - [r{relationName: \"%s\", kg_id: %d}] - (n2{id: %d}) RETURN r", entity1_id, relationName, kg_id, entity2_id);
            StatementResult checkResult = tx.run(checkCQL);
            if (checkResult.hasNext()) {
                System.out.println("CQL: " + checkCQL);
                System.out.println("关系已存在，无需添加");
                return;
            }
            String CQL = String.format("MATCH (n1:NODE{id: %d, kg_id: %d}), (n2:NODE{id: %d, kg_id: %d}) CREATE (n1) - [:RELATION{kg_id: %d, similarity: %f, relationName: \"%s\", sysKeys: \"%s\", sysValues: \"%s\", userKeys: \"%s\", userValues: \"%s\"}] -> (n2)", entity1_id, kg_id, entity2_id, kg_id, kg_id, similarity, relationName, sysKeys, sysValues, userKeys, userValues);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("单向关系添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，删除单向关系
     *
     * @param r 关系
     */
    public static void deleteRelation(Relation r) {
        //启动事务
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (n1:NODE{id: %d}) - [r{relationName: \"%s\", kg_id: %d}] -> (n2:NODE{id: %d}) DELETE r", r.getEntity1_id(), r.getRelationName(), r.getKg_id(), r.getEntity2_id());
            tx.run(CQL);
            //提交事务
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("单向关系删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，更新单向关系
     *
     * @param r 关系
     */
    public static void updateRelation(Relation r) {
        Long kg_id = r.getKg_id();
        double similarity = r.getSimilarity();
        String relationName = r.getRelationName();
        String sysKeys = r.getSysAttributeKeys();
        String sysValues = r.getSysAttributeValues();
        String userKeys = r.getUserAttributeKeys();
        String userValues = r.getUserAttributeValues();
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (n1:NODE{id: %d}) - [r1{kg_id: %d}] -> (n2:NODE{id: %d}) CREATE (n1) - " +
                            "[r2:%s] -> (n2) SET r2.entity_1 = %d, r2.entity_2 = %d, r2.kg_id = %d, r2.similarity =" +
                            " %f, r2.relationName = \"%s\", r2.sysKeys = \"%s\", r2.sysValues = \"%s\", r2.userKeys =" +
                            " \"%s\", r2.userValues = \"%s\" DELETE r1", r.getEntity1_id(), kg_id, r.getEntity2_id(),
                    relationName, r.getEntity1_id(), r.getEntity2_id(), kg_id, similarity, relationName, sysKeys,
                    sysValues, userKeys, userValues);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("单向关系更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cql命令，更新单向关系
     *
     * @param r 关系
     */
    public static void modifyRelation(Relation r,String oldLabel) {
        Long kg_id = r.getKg_id();
        String relationName = r.getRelationName();
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (n1:NODE{id: %d}) - [r1{kg_id: %d, relationName = \"%s\"}] -> (n2:NODE{id: %d}) CREATE (n1) - " +
                            "[r2:%s] -> (n2) SET r2.entity_1 = %d, r2.entity_2 = %d, r2.kg_id = %d, r2.relationName = " +
                            "\"%s\" DELETE r1", r.getEntity1_id(), kg_id,oldLabel, r.getEntity2_id(),
                    relationName, r.getEntity1_id(), r.getEntity2_id(), kg_id, relationName);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("单向关系更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空指定ID的图谱数据库
     *
     * @param kgId 图谱的ID
     * @throws Err 抛出异常
     */
    public static void clearCurrent(long kgId) throws Err {
        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            String CQL = String.format("MATCH (e1) - [r{kg_id: %d}] - (e2) DELETE e1, r, e2", kgId);
            tx.run(CQL);
            System.out.println("CQL: " + CQL);
            CQL = String.format("MATCH (e{kg_id: %d}) DELETE e", kgId);
            tx.run(CQL);
            tx.success();
            System.out.println("CQL: " + CQL);
            System.out.println("指定图谱已清空");
        } catch (Exception e) {
            throw new Err("图谱清空失败");
        }
    }
}

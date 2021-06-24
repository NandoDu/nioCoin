package niocoin.kgsystem.kgbackend.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import niocoin.kgsystem.kgbackend.dto.nreDTO;
import niocoin.kgsystem.kgbackend.enums.GraphStatus;
import niocoin.kgsystem.kgbackend.mapper.GraphMapper;
import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.Relation;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NREService {

    private final KafkaTemplate<String, String> template;
    private final GraphMapper graphMapper;

    public NREService(KafkaTemplate<String, String> template, GraphMapper graphMapper) {
        this.template = template;
        this.graphMapper = graphMapper;
    }

    public void pythonNRE(nreDTO nreDTO) {
        System.out.println(nreDTO.toString());
        if (nreDTO.getGraphId() == 0){
            return;
        }
        template.send("java2py", "nre" + "<==>" + nreDTO.getGraphId() + "<==>" + nreDTO.getText());
    }

    public void transformJson(String ereJson) {
        JSONObject jsonObject = JSONObject.parseObject(ereJson);
        long graphId = jsonObject.getLong("graphId");
        JSONArray data = jsonObject.getJSONArray("data");
        JSONArray singleNode = jsonObject.getJSONArray("entities");
        for (Object temp: singleNode){
            String name = (String) temp;
            Entity en = new Entity();
            en.setUserAttributeValues("");
            en.setSysAttributeValues("");
            en.setSysAttributeKeys("");
            en.setUserAttributeKeys("");
            en.setLabel(name);
            en.setKg_id(graphId);
            en.setId(Neo4jUtil.generateNewId(graphId));
            Neo4jUtil.addNode(en);
        }
        for (Object temp : data) {
            JSONObject ere = (JSONObject) temp;
            String node1Label = ere.getString("node1");
            String node2Label = ere.getString("node2");
            String relationLabel = ere.getString("relation");
            Neo4jUtil.addRelation(createRelation(graphId, node1Label, node2Label, relationLabel));
            System.out.println(node1Label + node2Label + relationLabel + graphId);
        }
        switch (graphMapper.getGraphStatus(graphId)) {
            case "FirstNRESent":
                graphMapper.updateGraphStatus(GraphStatus.FirstNREReceived.toString(), graphId);
                break;
            case "SecondNRESent":
                graphMapper.updateGraphStatus(GraphStatus.Created.toString(), graphId);
                break;
            default:
                System.out.println("test");
                break;
        }
    }

    public Entity createEntity(long graphId, String nodeLabel) {
        Entity entity = new Entity();
        entity.setId(Neo4jUtil.generateNewId(graphId));
        entity.setKg_id(graphId);
        entity.setLabel(nodeLabel);
        entity.setSysAttributeValues("");
        entity.setSysAttributeValues("");
        entity.setUserAttributeKeys("");
        entity.setUserAttributeValues("");
        return entity;
    }

    public Relation createRelation(long graphId, String node1Label, String node2Label, String relationLabel) {
        long node1Id = Neo4jUtil.checkEntityIdByEntityName(graphId, node1Label);
        long node2Id = Neo4jUtil.checkEntityIdByEntityName(graphId, node2Label);
        Relation relation = new Relation();
        relation.setRelationName(relationLabel);
        relation.setEntity1_id(node1Id);
        relation.setEntity2_id(node2Id);
        relation.setSimilarity(0.0);
        relation.setKg_id(graphId);
        relation.setSysAttributeKeys("");
        relation.setSysAttributeValues("");
        relation.setUserAttributeKeys("");
        relation.setUserAttributeValues("");
        return relation;
    }

}

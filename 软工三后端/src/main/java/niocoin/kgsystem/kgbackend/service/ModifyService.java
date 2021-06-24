package niocoin.kgsystem.kgbackend.service;

import niocoin.kgsystem.kgbackend.dto.modifyNodeDTO;
import niocoin.kgsystem.kgbackend.dto.modifyRelationDTO;
import niocoin.kgsystem.kgbackend.dto.nodeDTO;
import niocoin.kgsystem.kgbackend.dto.relationDTO;
import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.Relation;
import niocoin.kgsystem.kgbackend.util.Err;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModifyService {

    private final KafkaTemplate<String, String> template;

    public ModifyService(KafkaTemplate<String, String> template) {
        this.template = template;
    }

    private final String magicSplitter = "<==>";

    public String modifyNode(nodeDTO node) throws Err {
        Entity entity = new Entity();
        entity.setLabel(node.getNewLabel());
        entity.setKg_id(node.getGraphId());
        entity.setId(node.getNodeId());
        String sendData;
        switch (node.getActionType()) {
            case 1:
                try {
                    Neo4jUtil.deleteNode(entity);
                } catch (Exception e) {
                    throw new Err("节点删除失败");
                }
                sendData = "deleteNode" + magicSplitter + node.getGraphId() + magicSplitter + node.getNewLabel();
                template.send("java2py", sendData);
                return "节点删除成功";
            case 0:
                try {
                    Neo4jUtil.modifyNode(entity);
                } catch (Exception e) {
                    throw new Err("节点改名失败");
                }
                sendData = "modifyNode" + magicSplitter + node.getGraphId() + magicSplitter + node.getOldLabel() + magicSplitter + node.getNewLabel();
                template.send("java2py", sendData);
                return "节点改名成功";
            case 2:
                sendData = "addNode" + magicSplitter + node.getGraphId() + magicSplitter + node.getOldLabel();
                template.send("java2py",sendData);
                return "节点添加成功";
            case 3:
                return "just Test";
            default:
                throw new Err("系统不接受的操作类型");
        }
    }

    public String modifyRelation(relationDTO relation) throws Err {
        String sendData;
        Relation tempRelation = new Relation();
        tempRelation.setKg_id(relation.getGraphId());
        tempRelation.setRelationName(relation.getNewLabel());
        tempRelation.setEntity1_id(Neo4jUtil.checkEntityIdByEntityName(relation.getGraphId(), relation.getNode1Label()));
        tempRelation.setEntity2_id(Neo4jUtil.checkEntityIdByEntityName(relation.getGraphId(), relation.getNode2Label()));
        switch (relation.getActionType()) {
            case 1:
                try {
                    Neo4jUtil.deleteRelation(tempRelation);
                } catch (Exception e) {
                    throw new Err("关系删除失败");
                }
                sendData = "deleteEdge" + magicSplitter + relation.getGraphId() + magicSplitter + relation.getNode1Label() + magicSplitter + relation.getNewLabel() + magicSplitter + relation.getNode2Label();
                template.send("java2py", sendData);
                return "关系删除成功";
            case 0:
                try {
                    Neo4jUtil.modifyRelation(tempRelation, relation.getOldLabel());
                } catch (Exception e) {
                    throw new Err("关系改名失败");
                }
                sendData = "modifyEdge" + magicSplitter + relation.getGraphId() + magicSplitter + relation.getNode1Label() + magicSplitter + relation.getOldLabel() + magicSplitter + relation.getNewLabel() + magicSplitter + relation.getNode2Label();
                template.send("java2py", sendData);
                return "关系改名成功";
            case 2:
                return "just Test";
            default:
                throw new Err("系统不接受的操作类型");
        }
    }

    public String modifyNodes(modifyNodeDTO modifyNodeDTO) throws Err {
        List<nodeDTO> data = modifyNodeDTO.getData();
        for (nodeDTO temp: data){
            modifyNode(temp);
        }
        return "节点修改完成";
    }

    public String modifyRelations(modifyRelationDTO modifyRelationDTO) throws Err {
        List<relationDTO> data = modifyRelationDTO.getData();
        for (relationDTO temp: data){
            modifyRelation(temp);
        }
        return "节点修改完成";
    }

}

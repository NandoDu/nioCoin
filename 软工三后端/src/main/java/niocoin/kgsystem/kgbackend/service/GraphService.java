package niocoin.kgsystem.kgbackend.service;

import niocoin.kgsystem.kgbackend.dto.*;
import niocoin.kgsystem.kgbackend.enums.GraphStatus;
import niocoin.kgsystem.kgbackend.mapper.GraphMapper;
import niocoin.kgsystem.kgbackend.po.Entity;
import niocoin.kgsystem.kgbackend.po.Relation;
import niocoin.kgsystem.kgbackend.util.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class GraphService {

    final private GraphMapper graphMapper;
    final private OssUtil ossUtil;

    public GraphService(GraphMapper graphMapper, OssUtil ossUtil) {
        this.graphMapper = graphMapper;
        this.ossUtil = ossUtil;
    }

    private String setAttributeKeys(Map<String, String> attributes) {
        if (!attributes.isEmpty()) {
            StringBuilder attributeKeys = new StringBuilder();
            StringBuilder attributeValues = new StringBuilder();
            for (String key : attributes.keySet()) {
                attributeKeys.append(key);
                attributeKeys.append("<==>");
                attributeValues.append(attributes.get(key));
                attributeValues.append("<==>");
            }
            String res = attributeKeys.substring(0, attributeKeys.length() - 4);
            res += ">==<";
            res += attributeValues.substring(0, attributeValues.length() - 4);
            return res;
        } else {
            return "";
        }
    }

    public String parseJson(updateGraphDTO data) {
        for (node tempNode : data.getNodes()) {
            long id = Neo4jUtil.generateNewId(data.getKgId());
            Entity tempEntity = new Entity();
            tempEntity.setId(id);
            tempEntity.setLabel(tempNode.getLabel() == null ? "" : tempNode.getLabel());
            String KeysAndValues = setAttributeKeys(tempNode.getSysAttributes());
            if (KeysAndValues.equals("")) {
                tempEntity.setSysAttributeKeys("");
                tempEntity.setSysAttributeValues("");
            } else {
                String[] temp;
                temp = KeysAndValues.split(">==<");
                tempEntity.setSysAttributeKeys(temp[0]);
                tempEntity.setSysAttributeValues(temp[1]);
            }
            KeysAndValues = setAttributeKeys(tempNode.getUserAttributes());
            if (KeysAndValues.equals("")) {
                tempEntity.setUserAttributeKeys("");
                tempEntity.setUserAttributeValues("");
            } else {
                String[] temp;
                temp = KeysAndValues.split(">==<");
                tempEntity.setUserAttributeKeys(temp[0]);
                tempEntity.setUserAttributeValues(temp[1]);
            }
            tempEntity.setKg_id(data.getKgId());
            Neo4jUtil.addNode(tempEntity);
        }
        for (relation tempEdge : data.getEdges()) {
            Relation tempRelation = new Relation();
            tempRelation.setSimilarity(1.0);
            String inputSourceId = tempEdge.getSource();
            String inputTargetId = tempEdge.getTarget();
            node node1, node2;
            node1 = data.getNodes().stream().filter(node -> inputSourceId.equals(node.getId())).findAny().orElse(new node());
            node2 = data.getNodes().stream().filter(node -> inputTargetId.equals(node.getId())).findAny().orElse(new node());
            tempRelation.setEntity1_id(Neo4jUtil.checkEntityIdByEntityName(data.getKgId(), node1.getLabel()));
            tempRelation.setEntity2_id(Neo4jUtil.checkEntityIdByEntityName(data.getKgId(), node2.getLabel()));
            tempRelation.setKg_id(data.getKgId());
            tempRelation.setRelationName(tempEdge.getLabel() == null ? "" : tempEdge.getLabel());
            String keysAndValues = setAttributeKeys(tempEdge.getSysAttributes());
            if (keysAndValues.equals("")) {
                tempRelation.setSysAttributeKeys("");
                tempRelation.setSysAttributeValues("");
            } else {
                String[] temp;
                temp = keysAndValues.split(">==<");
                tempRelation.setSysAttributeKeys(temp[0]);
                tempRelation.setSysAttributeValues(temp[1]);
            }
            keysAndValues = setAttributeKeys(tempEdge.getUserAttributes());
            if (keysAndValues.equals("")) {
                tempRelation.setUserAttributeKeys("");
                tempRelation.setUserAttributeValues("");
            } else {
                String[] temp;
                temp = keysAndValues.split(">==<");
                tempRelation.setUserAttributeKeys(temp[0]);
                tempRelation.setUserAttributeValues(temp[1]);
            }
            Neo4jUtil.addRelation(tempRelation);
        }
        return "图谱更新成功";
    }

    public long GenerateNewGraph(long userId, String graphName) throws Err {
        long newKgId = graphMapper.getNewGraphId() + 1;
        try {
            graphMapper.createNewUserGraph(userId, newKgId, 1, 0);
            graphMapper.createNewGraph(newKgId, graphName, GetTime.getCurrentTime());
        } catch (Exception e) {
            graphMapper.rollBackUserGraph(userId, newKgId);
            graphMapper.rollBackGraph(newKgId);
            throw new Err("创建新图谱失败");
        }
        return newKgId;
    }

    public String updateGraphName(graphRenameDTO graphRenameDTO) throws Err {
        try {
            graphMapper.updateGraphName(graphRenameDTO.getGraphId(), graphRenameDTO.getGraphNewName(), GetTime.getCurrentTime());
        } catch (Exception e) {
            throw new Err("重命名图谱失败");
        }

        return "修改成功";
    }

    public List<graphInfoDTO> getAllGraph(long userId) {
        return graphMapper.selectAllGraph(userId);
    }
    public List<graphInfoDTO> getAllGraphByOrder(long userId) {
        return graphMapper.selectAllGraphByOrder(userId);
    }

    public List<graphInfoDTO> getStarredGraph(long userId) {
        return graphMapper.selectStarredGraph(userId);
    }

    public String setGraphStarred(long userId, long graphId, int starFlag) throws Err {
        try {
            graphMapper.setGraphStarred(graphId, userId, starFlag);
        } catch (Exception e) {
            throw new Err("设置星标失败");
        }
        return "success";
    }

    public String deleteGraph(long userId, long graphId) throws Err {
        try {
            if (checkAuth(userId, graphId)) {
                graphMapper.deleteUserGraph(graphId);
                graphMapper.deleteGraph(graphId);
            } else throw new Err("没有权限删除图谱");
        } catch (Exception e) {
            throw new Err("图谱删除失败");
        }
        return "成功删除图谱";
    }

    public boolean checkAuth(long userId, long graphId) throws Err {
        try {
            return graphMapper.checkAuth(userId, graphId) == 1;
        } catch (Exception e) {
            throw new Err("权限查询失败");
        }
    }

    public String getGraphName(long userId, long graphId) throws Err {
        String name;
        try {
            name = graphMapper.getGraphName(userId, graphId);
        } catch (Exception e) {
            throw new Err("图谱名字查询失败");
        }
        return name;
    }

    public void setGraphStatus(long graphId, GraphStatus graphStatus) {
        graphMapper.updateGraphStatus(graphStatus.toString(), graphId);
    }

    public String getGraphStatus(long graphId) {
        return graphMapper.getGraphStatus(graphId);
    }

    public void setThumbPicture(SaveThumbDTO saveThumbDTO, InputStream stream) throws Err {
        String url = ossUtil.upload("niocoin-181", saveThumbDTO.name, stream);
        graphMapper.setUrl(saveThumbDTO.graphId, url);
    }

}

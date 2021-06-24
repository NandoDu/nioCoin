package niocoin.kgsystem.kgbackend.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import niocoin.kgsystem.kgbackend.util.Neo4jUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JsonTransService {
    public JSONObject generateJSONByKgId(long kgId) {
        List<Map<String, Object>> entityList = Neo4jUtil.checkAllEntityByKgId(kgId);
        JSONArray entityJSONArray = new JSONArray();
        for (Map<String, Object> entity : entityList) {
            JSONObject entityJSONObject = new JSONObject();
            JSONObject entityAttributeJSONObject = new JSONObject();
            entityJSONObject.put("id", entity.get("id").toString());
            entityJSONObject.put("label", entity.get("label"));
            setKeysAndValues(entityJSONArray, entity, entityJSONObject, entityAttributeJSONObject);
        }
        List<Map<String, Object>> relationList = Neo4jUtil.checkAllRelationByKgId(kgId);
        JSONArray relationJSONArray = new JSONArray();
        for (Map<String, Object> relation : relationList) {
            JSONObject relationJSONObject = new JSONObject();
            JSONObject relationAttributeJSONObject = new JSONObject();
            relationJSONObject.put("source", relation.get("entity1_id").toString());
            relationJSONObject.put("target", relation.get("entity2_id").toString());
            relationJSONObject.put("label", relation.get("relationName"));
            setKeysAndValues(relationJSONArray, relation, relationJSONObject, relationAttributeJSONObject);
        }
        JSONObject transVueJSON = new JSONObject();
        transVueJSON.put("nodes", entityJSONArray);
        transVueJSON.put("edges", relationJSONArray);
        return transVueJSON;
    }

    private void setKeysAndValues(JSONArray inputJSONArray, Map<String, Object> inputMapData, JSONObject inputJSONObject, JSONObject attributeJSONObject) {
        String[] keys = ((String) inputMapData.get("sysKeys")).split("<==>");
        String[] values = ((String) inputMapData.get("sysValues")).split("<==>");
        if (keys.length != 0) {
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals("") || values[i].equals(""))
                    continue;
                attributeJSONObject.put(keys[i], values[i]);
            }
        }
        inputJSONObject.put("sysAttributes", attributeJSONObject);
        attributeJSONObject = new JSONObject();
        keys = ((String) inputMapData.get("userKeys")).split("<==>");
        values = ((String) inputMapData.get("userValues")).split("<==>");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals("") || values[i].equals(""))
                continue;
            attributeJSONObject.put(keys[i], values[i]);
        }
        inputJSONObject.put("userAttributes", attributeJSONObject);
        inputJSONArray.add(inputJSONObject);
    }
}

package niocoin.kgsystem.kgbackend.po;

public class Relation {
    private Long entity1_id;
    private Long entity2_id;
    private Long kg_id;
    private double similarity;
    private String relationName;
    private String sysAttributeKeys;
    private String sysAttributeValues;
    private String userAttributeKeys;
    private String userAttributeValues;

    public void setEntity1_id(Long entity1_id) {
        this.entity1_id = entity1_id;
    }

    public Long getEntity1_id() {
        return this.entity1_id;
    }

    public void setEntity2_id(Long entity2_id) {
        this.entity2_id = entity2_id;
    }

    public Long getEntity2_id() {
        return this.entity2_id;
    }

    public void setKg_id(Long kg_id) {
        this.kg_id = kg_id;
    }

    public Long getKg_id() {
        return kg_id;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getSysAttributeKeys() {
        return sysAttributeKeys;
    }

    public void setSysAttributeKeys(String sysAttributeKeys) {
        this.sysAttributeKeys = sysAttributeKeys;
    }

    public String getSysAttributeValues() {
        return sysAttributeValues;
    }

    public void setSysAttributeValues(String sysAttributeValues) {
        this.sysAttributeValues = sysAttributeValues;
    }

    public String getUserAttributeKeys() {
        return userAttributeKeys;
    }

    public void setUserAttributeKeys(String userAttributeKeys) {
        this.userAttributeKeys = userAttributeKeys;
    }

    public String getUserAttributeValues() {
        return userAttributeValues;
    }

    public void setUserAttributeValues(String userAttributeValues) {
        this.userAttributeValues = userAttributeValues;
    }
}

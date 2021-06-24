package niocoin.kgsystem.kgbackend.po;

public class Entity {
    private Long id;
    private Long kg_id;
    private String label;
    private String sysAttributeKeys;
    private String sysAttributeValues;
    private String userAttributeValues;
    private String userAttributeKeys;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKg_id() {
        return kg_id;
    }

    public void setKg_id(Long kg_id) {
        this.kg_id = kg_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public String getUserAttributeValues() {
        return userAttributeValues;
    }

    public void setUserAttributeValues(String userAttributeValues) {
        this.userAttributeValues = userAttributeValues;
    }

    public String getUserAttributeKeys() {
        return userAttributeKeys;
    }

    public void setUserAttributeKeys(String userAttributeKeys) {
        this.userAttributeKeys = userAttributeKeys;
    }
}

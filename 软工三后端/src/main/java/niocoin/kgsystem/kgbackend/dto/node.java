package niocoin.kgsystem.kgbackend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class node {
    private String id;
    private String label;
    private Map<String, String> sysAttributes;
    private Map<String, String> userAttributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getSysAttributes() {
        return sysAttributes;
    }

    public void setSysAttributes(Map<String, String> sysAttributes) {
        this.sysAttributes = sysAttributes;
    }

    public Map<String, String> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(Map<String, String> userAttributes) {
        this.userAttributes = userAttributes;
    }
}

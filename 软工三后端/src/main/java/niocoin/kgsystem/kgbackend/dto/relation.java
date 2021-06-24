package niocoin.kgsystem.kgbackend.dto;

import java.util.Map;

public class relation {
    private String source;
    private String target;
    private String label;
    private Map<String, String> sysAttributes;
    private Map<String, String> userAttributes;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

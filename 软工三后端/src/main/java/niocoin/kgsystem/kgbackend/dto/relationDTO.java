package niocoin.kgsystem.kgbackend.dto;

public class relationDTO {
    long graphId;
    String oldLabel;
    String newLabel;
    int actionType;//1是删除，0是修改
    String node1Label;
    String node2Label;

    public String getNode1Label() {
        return node1Label;
    }

    public void setNode1Label(String node1Label) {
        this.node1Label = node1Label;
    }

    public String getNode2Label() {
        return node2Label;
    }

    public void setNode2Label(String node2Label) {
        this.node2Label = node2Label;
    }

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }

    public String getOldLabel() {
        return oldLabel;
    }

    public void setOldLabel(String oldLabel) {
        this.oldLabel = oldLabel;
    }

    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public relationDTO() {
    }

    public relationDTO(long graphId, String oldLabel, String newLabel, int actionType, String node1Label, String node2Label) {
        this.graphId = graphId;
        this.oldLabel = oldLabel;
        this.newLabel = newLabel;
        this.actionType = actionType;
        this.node1Label = node1Label;
        this.node2Label = node2Label;
    }
}

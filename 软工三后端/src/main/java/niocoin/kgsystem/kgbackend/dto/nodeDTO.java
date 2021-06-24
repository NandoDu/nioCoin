package niocoin.kgsystem.kgbackend.dto;

public class nodeDTO {
    int actionType; // 1是删除，0是修改，2是添加
    long graphId;// 图谱ID
    long nodeId; // 节点ID
    String newLabel;// 节点名称
    String oldLabel;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }

    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public String getOldLabel() {
        return oldLabel;
    }

    public void setOldLabel(String oldLabel) {
        this.oldLabel = oldLabel;
    }

    public nodeDTO() {
    }

    public nodeDTO(int actionType, long graphId, long nodeId, String newLabel, String oldLabel) {
        this.actionType = actionType;
        this.graphId = graphId;
        this.nodeId = nodeId;
        this.newLabel = newLabel;
        this.oldLabel = oldLabel;
    }
}

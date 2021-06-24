package niocoin.kgsystem.kgbackend.dto;

public class graphRenameDTO {
    long graphId;
    String graphNewName;

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }

    public String getGraphNewName() {
        return graphNewName;
    }

    public void setGraphNewName(String graphNewName) {
        this.graphNewName = graphNewName;
    }

    public graphRenameDTO() {
    }

    public graphRenameDTO(long graphId, String graphNewName) {
        this.graphId = graphId;
        this.graphNewName = graphNewName;
    }
}

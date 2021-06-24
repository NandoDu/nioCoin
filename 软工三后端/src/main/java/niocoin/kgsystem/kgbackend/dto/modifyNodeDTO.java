package niocoin.kgsystem.kgbackend.dto;

import java.util.ArrayList;
import java.util.List;

public class modifyNodeDTO {
    List<nodeDTO> data = new ArrayList<>();
    long graphId;

    public List<nodeDTO> getData() {
        return data;
    }

    public void setData(List<nodeDTO> data) {
        this.data = data;
    }

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }

    public modifyNodeDTO() {
    }

    public modifyNodeDTO(List<nodeDTO> data, long graphId) {
        this.data = data;
        this.graphId = graphId;
    }
}

package niocoin.kgsystem.kgbackend.dto;

import java.util.ArrayList;
import java.util.List;

public class modifyRelationDTO {
    List<relationDTO> data = new ArrayList<>();
    long graphId;

    public List<relationDTO> getData() {
        return data;
    }

    public void setData(List<relationDTO> data) {
        this.data = data;
    }

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }


    public modifyRelationDTO() {
    }

    public modifyRelationDTO(List<relationDTO> data, long graphId) {
        this.data = data;
        this.graphId = graphId;
    }
}

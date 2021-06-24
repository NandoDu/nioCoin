package niocoin.kgsystem.kgbackend.dto;

import java.util.ArrayList;
import java.util.List;

public class graphDTO {
    private List<node> nodes = new ArrayList<>();
    private List<relation> edges = new ArrayList<>();

    public List<node> getNodes() {
        return nodes;
    }

    public void setNodes(List<node> nodes) {
        this.nodes = nodes;
    }

    public List<relation> getEdges() {
        return edges;
    }

    public void setEdges(List<relation> edges) {
        this.edges = edges;
    }
}

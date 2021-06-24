package niocoin.kgsystem.kgbackend.dto;

public class nreDTO {
    String text;
    long graphId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }

    public nreDTO() {
    }

    public nreDTO(String text, long graphId) {
        this.text = text;
        this.graphId = graphId;
    }

    @Override
    public String toString() {
        return "nreDTO{" +
            "text='" + text + '\'' +
            ", graphId=" + graphId +
            '}';
    }

}

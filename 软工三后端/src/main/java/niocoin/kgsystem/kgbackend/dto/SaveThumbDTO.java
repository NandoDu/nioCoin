package niocoin.kgsystem.kgbackend.dto;

public class SaveThumbDTO {

    public Long graphId;
    public String name;

    @Override
    public String toString() {
        return "SaveThumbDTO{" +
            "graphId=" + graphId +
            ", name='" + name + '\'' +
            '}';
    }

}

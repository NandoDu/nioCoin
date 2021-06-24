package niocoin.kgsystem.kgbackend.mapper;

import niocoin.kgsystem.kgbackend.dto.graphInfoDTO;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface GraphMapper {

    @Select("select coalesce(MAX(graphId),0) from Graph")
    int getNewGraphId();

    @Insert("insert into userGraph(userId,graphId,owned,starred) values (#{userId}, #{graphId},#{owned},#{starred})")
    void createNewUserGraph(@Param("userId") long userId, @Param("graphId") long graphId, @Param("owned") int owned, @Param("starred") int starred);

    @Delete("delete from userGraph where userId=#{userId} and graphId=#{graphId}")
    void rollBackUserGraph(@Param("userId") long userId, @Param("graphId") long graphId);

    @Insert("insert into Graph(graphId,graphName,time) values (#{graphId},#{graphName},#{time})")
    void createNewGraph(@Param("graphId") long graphId, @Param("graphName") String graphName, @Param("time") Timestamp time);

    @Delete("delete from Graph where graphId=#{graphId}")
    void rollBackGraph(@Param("graphId") long graphId);

    @Update("update Graph set graphName=#{graphName},time=#{time} where graphId=#{graphId}")
    void updateGraphName(@Param("graphId") long graphId, @Param("graphName") String graphName, @Param("time") Timestamp time);

    @Insert("insert into userGraph(userId,graphId,owned,starred) values (#{userId}, #{graphId},#{owned},#{starred})")
    void addUserToGraph(@Param("userId") long userId, @Param("graphId") long graphId, @Param("owned") int owned, @Param("starred") int starred);

    @Select("select graphId, graphName, time, imgURL, starred, owned from Graph join userGraph using (graphId) where userId=#{userId} order by graphId")
    List<graphInfoDTO> selectAllGraph(@Param("userId") long userId);

    @Select("select graphId, graphName, time, imgURL, starred, owned from Graph join userGraph using (graphId) where userId=#{userId} order by time DESC")
    List<graphInfoDTO> selectAllGraphByOrder(@Param("userId") long userId);

    @Select("select graphId, graphName, time, imgURL, starred, owned from Graph join userGraph using (graphId) where userId=#{userId} and starred=1 order by graphId")
    List<graphInfoDTO> selectStarredGraph(@Param("userId") long userId);

    @Update("update userGraph set starred=#{starFlag} where graphId=#{graphId} and userId=#{userId}")
    void setGraphStarred(@Param("graphId") long graphId, @Param("userId") long userId, @Param("starFlag") int starFlag);

    @Delete("delete from userGraph where graphId=#{graphId}")
    void deleteUserGraph(@Param("graphId") long graphId);

    @Delete("delete from Graph where graphId=#{graphId}")
    void deleteGraph(@Param("graphId") long graphId);

    @Select("select owned from userGraph where userId=#{userId} and graphId=#{graphId}")
    int checkAuth(@Param("userId") long userId, @Param("graphId") long graphId);

    @Select("select graphName from userGraph join Graph using (graphId) where userId=#{userId} and graphId=#{graphId}")
    String getGraphName(@Param("userId") long userId, @Param("graphId") long graphId);

    @Update("update Graph set status=#{status} where graphId=#{graphId}")
    void updateGraphStatus(@Param("status") String status, @Param("graphId") long graphId);

    @Select("select status from Graph where graphId=#{graphId}")
    String getGraphStatus(@Param("graphId") long graphId);

    @Update("update Graph set `imgURL`=#{url} where graphId=#{id}")
    void setUrl(@Param("id") Long graphId, @Param("url") String url);

}

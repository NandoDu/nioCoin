package niocoin.kgsystem.kgbackend.mapper;

import niocoin.kgsystem.kgbackend.po.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface AccountMapper {

    @Select("insert into User(email,password,username)\n" +
            "        values(#{email},#{password},#{username})")
    void createNewAccount(User user);

    @Select("select * from User where email=#{email}")
    User getAccountByEmail(@Param("email") String email);

    @Select("select * from User where username=#{username}")
    User getAccountByUsername(@Param("username") String username);

    @Delete("delete from User where username=#{username}")
    void deleteUserByUsername(@Param("username") String username);

    @Update("update User set password=#{password} where username=#{username}")
    void updatePass(@Param("password") String password,@Param("username") String username);

    @Update("update User set username=#{newUsername} where username=#{oldUsername}")
    void updateUsername(@Param("newUsername") String newUsername,@Param("oldUsername") String oldUsername);

    @Update("update User set imgUrl=#{imgUrl} where id=#{userId}")
    void updateUserImg(@Param("imgUrl") String imgUrl, @Param("userId") long userId);
}

package niocoin.kgsystem.kgbackend.dto;

public class forgetPasswordDTO {
    String username;
    String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public forgetPasswordDTO() {
    }

    public forgetPasswordDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

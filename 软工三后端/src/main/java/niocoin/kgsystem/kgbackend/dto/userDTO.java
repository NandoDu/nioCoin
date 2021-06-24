package niocoin.kgsystem.kgbackend.dto;

public class userDTO {
    String username;
    String password;
    String email;
    String verCode;

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public userDTO(){};

    public userDTO(String username, String password,String email,String verCode){
        this.username = username;
        this.password = password;
        this.email = email;
        this.verCode = verCode;
    }
}

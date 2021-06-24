package niocoin.kgsystem.kgbackend.dto;

public class verifyUserEmailDTO {
    String username;
    String email;
    String verCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

    public verifyUserEmailDTO() {
    }

    public verifyUserEmailDTO(String username, String email, String verCode) {
        this.username = username;
        this.email = email;
        this.verCode = verCode;
    }
}

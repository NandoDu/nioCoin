package niocoin.kgsystem.kgbackend.dto;

public class passwordDTO {
    String userName;
    String oldPass;
    String newPass;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public passwordDTO() {
    }

    public passwordDTO(String userName, String oldPass, String newPass) {
        this.userName = userName;
        this.oldPass = oldPass;
        this.newPass = newPass;
    }
}

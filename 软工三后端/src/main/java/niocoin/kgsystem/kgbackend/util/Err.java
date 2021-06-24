package niocoin.kgsystem.kgbackend.util;

public class Err extends Exception {
    public String msg;
    public Err(String msg){
        super();
        this.msg = "[错误] " + msg;
    }
}

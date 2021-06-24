package niocoin.kgsystem.kgbackend.util;

public class Res {

    public Object data;
    public boolean success;
    public String msg;

    public static Res ok(Object data) {
        Res r = new Res();
        r.data = data;
        r.success = true;
        return r;
    }

    public static Res ok() {
        return ok(null);
    }

    public static Res oops(String msg) {
        Res r = new Res();
        r.data = null;
        r.msg = msg;
        r.success = false;
        return r;
    }

}

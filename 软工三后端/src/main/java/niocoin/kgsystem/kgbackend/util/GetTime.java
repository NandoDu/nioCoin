package niocoin.kgsystem.kgbackend.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class GetTime {
    public static Timestamp getCurrentTime(){
        return Timestamp.valueOf(LocalDateTime.now());
    }
}

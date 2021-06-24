package niocoin.kgsystem.kgbackend.enums;

public enum GraphStatus {
    JustCreate, //刚刚创建
    FirstNRESent, // 第一次NRE请求已发送
    FirstNREReceived, // 第一次NRE请求已收到结果
    UserModify, // 用户正在对第一次NRE结果进行修改
    SecondNRESent,//  第二次NRE请求已发送
    Created // 图谱创建完成
}


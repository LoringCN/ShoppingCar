package cn.huace.message.enums;

/**
 * 处理状态码
 * Created by yld on 2017/10/23.
 */
public enum HandleCode {
    SUCCESS(0),FAILURE(1);
    private int value;
    HandleCode(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
}

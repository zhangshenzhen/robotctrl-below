package com.bean.serialport;

/**
 * Created by lx on 2017/2/14.
 */

public class UserInfo {
     //刷身份证的字段
    String name;
    String id;
    String Address;


    String number;
    String tel;
    String Email;
    String relative;


    private static UserInfo user;
    private UserInfo(){};
    public static UserInfo getInstance(){
        if(user==null){
            synchronized (UserInfo.class){
                if (user==null){
                    user = new UserInfo();
                }
            }
        }
       return  user;
    }
}

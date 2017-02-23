package com.bean.serialport;

/**
 * Created by lx on 2017/2/14.
 */

public class UserInfo {
     //刷身份证的字段
    String name;
    String id;
    String Address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    //第二页面采集
    String residentialtel;
    String companytel;
    String phone;

    public String getResidentialtel() {
        return residentialtel;
    }

    public void setResidentialtel(String residentialtel) {
        this.residentialtel = residentialtel;
    }

    public String getCompanytel() {
        return companytel;
    }

    public void setCompanytel(String companytel) {
        this.companytel = companytel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

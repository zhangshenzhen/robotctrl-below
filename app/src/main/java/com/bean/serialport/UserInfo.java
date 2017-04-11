package com.bean.serialport;

/**
 * Created by lx on 2017/2/14.
 */
public class UserInfo {
     //刷身份证的字段
    String name;
    String id;
    String companyaddress;

    public String getCompanyaddress() {
        return companyaddress;
    }

    String companytel;
    String residentialtel;
    String phone;

    public String getCompanytel() {
        return companytel;
    }

    public String getResidentialtel() {
        return residentialtel;
    }

    public String getPhone() {
        return phone;
    }


    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCompanyaddress(String companyaddress){
        this.companyaddress = companyaddress;
    }

    public void setCompanytel(String companytel) {
        this.companytel = companytel;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setResidentialtel(String residentialtel){
        this.residentialtel = residentialtel;
    }
   //第二页字段
    String emaill;//邮箱
    String province;//省
    String city;//市
    String street;//街道
    String national;//国籍

    public String getMarriage() {
        return marriage;
    }

    String marriage;//婚姻
    String education;//学历
    String residentialaddress;//住宅地址
    String residentialtype;//住宅类型
    String residentialyeare;//住宅年限

    public void setEducation(String education) {
        this.education = education;
    }
    public void setResidentialyeare(String residentialyeare) {
        this.residentialyeare = residentialyeare;
    }
    public void setResidentialtype(String residentialtype) {
        this.residentialtype = residentialtype;
    }
    public void setResidentialaddress(String residentialaddress) {
        this.residentialaddress = residentialaddress;
    }
    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public void setNational(String national) {
        this.national = national;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public void setEmaill(String emaill) {
        this.emaill = emaill;
    }

    //第三页字段
    String  kinName;             //亲属姓名
    String  kinTel;              //亲属电话
    String  kinPhone;            //亲属电话
    String  relatives;           //亲属关系
    String  emergencyContact;    //紧急联系人
    String  cardholderrelatives; //持卡人关系关系
    String  emergencyContactphone;//紧急联系人电话

    public void setEmergencyContactphone(String emergencyContactphone) {
        this.emergencyContactphone = emergencyContactphone;
    }
    public void setCardholderrelatives(String cardholderrelatives) {
        this.cardholderrelatives = cardholderrelatives;
    }
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    public void setRelatives(String relatives) {
        this.relatives = relatives;
    }
    public void setKinPhone(String kinPhone) {
        this.kinPhone = kinPhone;
    }
    public void setKinTel(String kinTel) {
        this.kinTel = kinTel;
    }
    public void setKinName(String kinName) {
        this.kinName = kinName;
    }

     //第四页字段
    String companyName;       //公司名称;
    String elpmoyeeNum;       //公司员工数
    String getCompanyaddress; //单位地址;
    String  businessnature;   //行业性质
    String companyNature;     //公司性质
    String branch;            //部门
    String  position ;        //职位
    String  level;            //等级
    String  salaryYesr ;      //年薪
    String workYears;         //任职难数
    String security;          //社保
    String professsitional;   //职称

    public void setElpmoyeeNum(String elpmoyeeNum) {
        this.elpmoyeeNum = elpmoyeeNum;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setGetCompanyaddress(String getCompanyaddress) {
        this.getCompanyaddress = getCompanyaddress;
    }
    public void setBusinessnature(String businessnature) {
        this.businessnature = businessnature;
    }
    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public void setSalaryYesr(String salaryYesr) {
        this.salaryYesr = salaryYesr;
    }
    public void setWorkYears(String workYears) {
        this.workYears = workYears;
    }
    public void setSecurity(String security) {
        this.security = security;
    }
    public void setProfesssitional(String professsitional) {
        this.professsitional = professsitional;
    }

    private static UserInfo user;
    public static void setUser(UserInfo user) {
        UserInfo.user = user;
    }
 /*   public static UserInfo getUser(UserInfo getInstance) {
          return getInstance;
    }*/
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

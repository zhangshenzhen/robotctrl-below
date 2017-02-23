package com.bean.serialport;

import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lx on 2017/2/17.
 */

public class CardInfoBean    implements  Serializable{
   public String title;
   public List<String> body;
   public String waist;
   public List<Waist> waists;
   public String leg;
   public List<String> foot;

    @Override
    public String toString() {
        return "CardInfoBean{" +
                "title='" + title + '\'' +
                ", body=" + body +
                ", waist='" + waist + '\'' +
                ", waists=" + waists +
                ", leg='" + leg + '\'' +
                ", foot=" + foot +
                '}';
    }

    public CardInfoBean() {

    }

    public CardInfoBean(String title, List<String> body, String waist, List<Waist> waists, String leg, List<String> foot) {
        this.title = title;
        this.body = body;
        this.waist = waist;
        this.waists = waists;
        this.leg = leg;
        this.foot = foot;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public void setWaists(List<Waist> waists) {
        this.waists = waists;
    }

    public void setLeg(String leg) {
        this.leg = leg;
    }

    public void setFoot(List<String> foot) {
        this.foot = foot;
    }

    public static class Waist {
        //币种

        public  Waist(int type, long money) {
            this.type = type;
            this.money = money;
        }

      public int type;
      public long money;
    }

}

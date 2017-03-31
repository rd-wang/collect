package com.master.bean;

import android.location.Location;

import java.io.Serializable;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2017/1/11
 */
public class LocaDate implements Serializable{

    public double speed;//速度

    public double lat;//纬度

    public double lng;//经度

    public long time;//时间

    public boolean isworn = true;//是否有效

    private LocaDate(Builder builder) {
        this.speed = builder.speed;
        this.lat = builder.lat;
        this.lng = builder.lng;
        this.time = builder.time;
        this.isworn = builder.isworn;
    }

    @Override
    public String toString() {
        return "locaDate{" +
            "isworn=" + isworn +
            ", speed=" + speed +
            ", lat=" + lat +
            ", lng=" + lng +
            ", time=" + time +
            '}';
    }


    public static class Builder {

        private double speed;//速度

        private double lat;//纬度

        private double lng;//经度

        private long time;//时间

        private boolean isworn = true;//是否损坏


        /**
         * 通过Point对象构建一个位置对象
         *
         * @param loc Location
         */
        public Builder initSite(Location loc) {
            if (loc != null) {
                this.lat = loc.getLatitude();
                this.lng = loc.getLongitude();
                this.time = loc.getTime();
                this.speed = loc.getSpeed();
                this.isworn = false;
                return this;
            } else {
                this.isworn = true;
                return this;
            }
        }

        /**
         * 通过Point对象构建一个位置对象
         *
         * @param p 点坐标
         */
        public Builder initSite(Point p) {
            if (p != null) {
                this.lat = p.x;
                this.lng = p.y;
                this.isworn = false;
            } else {
                this.isworn = true;
            }

            return this;
        }


        public LocaDate builder() {
            if (isworn) {
                return null;
            } else {
                return new LocaDate(this);
            }
        }
    }


}

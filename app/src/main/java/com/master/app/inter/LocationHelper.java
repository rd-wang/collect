package com.master.app.inter;

import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/19
 */
public interface LocationHelper {

    void updateLocation(Location location);//位置信息发生改变

    void updateStatus(String provider, int status, Bundle extras);//位置状态发生改变

    void updateGPSStatus(int event, GpsStatus pGpsStatus);//GPS状态发生改变

}

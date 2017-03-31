package com.master.ui.activity.map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.master.R;
import com.master.app.tools.ToastUtils;
import com.master.bean.LocaDate;
import com.master.constant.Const;
import com.master.contract.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class LocationActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.map)
    MapView map;


    private GraphicsLayer gLayerGps;

    private final String url
            = "http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer";

    private ArcGISTiledMapServiceLayer layer;

    private LocaDate mLocation;

    private String mDrawType;

    private List<LocaDate> mLocationList;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        title.setText("详细位置");
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            ToastUtils.showToast("数据错误");
            finish();
        } else {
            mDrawType = extras.getString(Const.SHOW_ATTR_TYPE);
            mLocation = (LocaDate) extras.getSerializable(Const.SHOW_ATTR_DATA_POINT);
            mLocationList = (ArrayList<LocaDate>) extras.get(Const.SHOW_ATTR_DATA_POINT_LIST);
        }

        layer = new ArcGISTiledMapServiceLayer(url);
        map.addLayer(layer);
        gLayerGps = new GraphicsLayer();
        map.addLayer(gLayerGps);
        //设置背景
        map.setMapBackground(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.white), 0, 0);
        map.setOnStatusChangedListener((OnStatusChangedListener) (o, status) -> {
            if (status.equals(OnStatusChangedListener.STATUS.INITIALIZED)) {
                //初始化完成才显示，防止黑屏
                map.setVisibility(View.VISIBLE);
                map.postDelayed(this::showGpsOnMap, 100);
            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_location;
    }

    /**
     * 将GPS点标注在地图上
     */
    public void showGpsOnMap() {

        //清空定位图层
        gLayerGps.removeAll();
        if (Const.DrawType.POINT.equals(mDrawType)) {
            Point wgspoint = new Point(mLocation.lng, mLocation.lat);
            Point mapPoint = (Point) GeometryEngine
                    .project(wgspoint, SpatialReference.create(4326), map.getSpatialReference());
            //图层的创建
            Graphic graphic = new Graphic(mapPoint,
                    new SimpleMarkerSymbol(Color.RED, 18, SimpleMarkerSymbol.STYLE.CIRCLE));
            gLayerGps.addGraphic(graphic);
            map.centerAt(mapPoint, true);

            map.setScale(8000);
        } else {
            List<Point> pointList = new ArrayList<>();
            SpatialReference spatialReference1 = SpatialReference.create(4326);
            SpatialReference spatialReference = map.getSpatialReference();
            for (int i = 0; i < mLocationList.size(); i++) {
                pointList.add(
                        (Point) GeometryEngine.project(new Point(mLocationList.get(i).lng + 0.0001 * i, mLocationList.get(i).lat + 0.0001 * i)
                                , spatialReference1
                                , spatialReference));
            }

            Log.e("show list  size", pointList.size() + "");
            Graphic graphic = null;
            Envelope envelope = null;
            MultiPath multipath = null;
            if (pointList.size() > 1) {
                multipath = new Polyline();
                multipath.startPath(pointList.get(0));
                for (int i = 1; i < mLocationList.size(); i++) {
                    multipath.lineTo(pointList.get(i));
                }
                graphic = new Graphic(multipath, new SimpleLineSymbol(Color.RED, 5));
                envelope = new Envelope();
                multipath.queryEnvelope(envelope);
            }
            gLayerGps.addGraphic(graphic);
            map.setExtent(multipath,100);
        }
    }


}

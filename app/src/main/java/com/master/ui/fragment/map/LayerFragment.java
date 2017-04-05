package com.master.ui.fragment.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.query.QueryParameters;
import com.master.R;
import com.master.app.RestricMsg;
import com.master.app.manager.RecordingMedium;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.CommonUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.ToastUtils;
import com.master.bean.LdData;
import com.master.bean.LocaDate;
import com.master.constant.Const;
import com.master.model.LayerModel;
import com.master.presenter.LayerPresenter;
import com.master.ui.activity.map.MrLayerActivity;
import com.master.ui.adapter.AttributeListAdapter;
import com.master.ui.adapter.SearchResultAdapter;
import com.master.ui.fragment.MvpFragment;
import com.master.ui.viewer.LayerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

import static com.master.R.id.search_content;


/**
 * @author Litao-pc on 2016/10/27.
 *         ~
 */
public class LayerFragment extends MvpFragment<LayerPresenter> implements LayerView {

    @BindString(R.string.basemap_url)
    String BASEURL;

    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.search_menu)
    Spinner searchMenu;

    @BindView(search_content)
    EditText searchContent;

    @BindView(R.id.search_btn)
    ImageView searchBtn;

    @BindView(R.id.search_result_layout)
    LinearLayout mSearchResultLayout;

    @BindView(R.id.search_result_list)
    RecyclerView mSearchResultList;

    public static String filepath = "";

    public static LayerFragment S_LayerFragment;

    Geodatabase geodatabase = null;

    int j = 0;

    int k = 0;

    int m = 0;

    Boolean ifidentify = false;

    public GraphicsLayer mGraphicsLayerIdentify = null;

    GraphicsLayer mGraphicsLayermeasure = null;

    SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(Color.RED, 5);

    SimpleMarkerSymbol mRedMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 20,
            SimpleMarkerSymbol.STYLE.CIRCLE);

    //列表所需的数据源
    final String[] geometryTypes = new String[]{Const.DrawType.POINT, Const.DrawType.POLYLINE, Const.DrawType.POLYGON};

    MyTouchListener myListener = null;

    LocationManager locMan = null;

    String myprovider = LocationManager.GPS_PROVIDER;

    LocationListener locationListener = null;

    Handler handler;

    double lat;

    double lng;

    int startindex = 0;

    //地图上展示的路径
    public MultiPath multipath;

    public Point lastPoint;

    Envelope envelope0 = new Envelope();

    public int i = 0;
    private SearchResultAdapter searchResultAdapter;
    private List<LdData> searchLxList;

    @Override
    protected LayerPresenter createPresenter() {
        return new LayerPresenter(new LayerModel());
    }

    @Override
    public void bindView(Bundle savedInstanceState) {
        S_LayerFragment = this;
        //去除水印
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
        ArcGISTiledMapServiceLayer dynamicLayout = new ArcGISTiledMapServiceLayer(BASEURL);
        dynamicLayout.setName("背景底图");
        mMapView.addLayer(dynamicLayout);

        mGraphicsLayerIdentify = new GraphicsLayer();
        mGraphicsLayerIdentify.setName("查询显示图层");
        mMapView.addLayer(mGraphicsLayerIdentify);
        mGraphicsLayermeasure = new GraphicsLayer();
        mGraphicsLayermeasure.setName("测量图层");
        mMapView.addLayer(mGraphicsLayermeasure);
        Envelope envelope = new Envelope();
        envelope.setXMin(8176078.237520734);
        envelope.setYMin(379653.9541849808);
        envelope.setXMax(15037685.88562758);
        envelope.setYMax(7086873.419584352);
        mMapView.setExtent(envelope);
        // Set tap listener for MapView
        myListener = new MyTouchListener(mContext, mMapView);
        //给mapView添加了触摸事件监听
        mMapView.setOnTouchListener(myListener);
        // TODO handle rotation


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    mGraphicsLayermeasure.removeAll();
                } else {
                    super.handleMessage(msg);
                }
            }
        };
        //初始化搜索框
        String[] mSearchSource = getResources().getStringArray(R.array.search_source);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner_textview, mSearchSource);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchMenu.setAdapter(adapter);
        searchMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSearchResultList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mSearchResultList.setItemAnimator(new DefaultItemAnimator());
        searchResultAdapter = new SearchResultAdapter();
        mSearchResultList.setAdapter(searchResultAdapter);
        searchResultAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LdData ldData = searchLxList.get(position);
                ArrayList<LocaDate> locaDates = DbHelperDbHelper.open().querySearchLD(ldData.getSSLX(), ldData.getQDJWDID(), ldData.getZDJWDID(), RecordingMedium.getWorkMapId());
                showSearchLd(locaDates);
                mSearchResultLayout.setVisibility(View.GONE);
            }
        });
        searchContent.clearFocus();
    }

    public void identify(float x, float y) {
        mGraphicsLayerIdentify.removeAll();
        if (ifidentify && geodatabase != null) {
            j = 0;
            m = 0;
            // convert event into screen click
            Point pointClicked = mMapView.toMapPoint(x, y);
            Polygon pg = GeometryEngine.buffer(pointClicked, mMapView.getSpatialReference(), 500,
                    mMapView.getSpatialReference().getUnit()); //null表示采用地图单位
            List getdatatables = geodatabase.getGeodatabaseTables();
            GeodatabaseFeatureTable geodatabaseFeatureTable = null;
            FeatureLayer featureLayer = null;
            QueryParameters query = new QueryParameters();
            query.setGeometry(pg);
            query.setReturnGeometry(true);
            query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
            query.setInSpatialReference(mMapView.getSpatialReference());
            query.setOutFields(new String[]{"*"});
            query.setMaxAllowableOffset(500);
            for (int i = 0; i < getdatatables.size() && m == 0; i++) {
                //create a feature layer
                geodatabaseFeatureTable = (GeodatabaseFeatureTable) getdatatables.get(i);
                featureLayer = new FeatureLayer(geodatabaseFeatureTable);
                if ((featureLayer.getMinScale() > mMapView.getScale()
                        || featureLayer.getMinScale() == 0) && featureLayer.isVisible()) {
                    geodatabaseFeatureTable
                            .queryFeatures(query, new CallbackListener<FeatureResult>() {
                                public void onCallback(FeatureResult featureSet) {
                                    Feature feature = null;
                                    if (featureSet != null && m == 0) {
                                        Iterator iter = featureSet.iterator();
                                        while (iter.hasNext() && m == 0) {
                                            feature = (Feature) iter.next();
                                            m = 1;
                                            break;
                                        }
                                        if (feature != null) {
                                            m = 1;
                                            ArrayList arraylist = new ArrayList();
                                            Map attrs = feature.getAttributes();
                                            Set set = attrs.entrySet();
                                            Iterator keyiter = set.iterator();
                                            while (keyiter.hasNext()) {
                                                ArrayList arraylist1 = new ArrayList();
                                                Map.Entry entry = (Map.Entry) keyiter.next();
                                                arraylist1.add(0, entry.getKey());
                                                arraylist1.add(1, entry.getValue().toString());
                                                arraylist.add(arraylist1);
                                            }
                                            // show the editor dialog.
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    // show the editor dialog.
                                                    onCreatePopupWindow(arraylist);
                                                }
                                            });
                                            //点击后高亮显示选中对象
                                            Graphic graphic = null;
                                            if (Const.DrawType.POINT.equals(feature.getGeometry().getType().toString())) {
                                                graphic = new Graphic(feature.getGeometry(), mRedMarkerSymbol);
                                            } else if (Const.DrawType.POLYLINE.equals(feature.getGeometry().getType().toString())) {
                                                graphic = new Graphic(feature.getGeometry(), simpleLineSymbol);
                                            }
                                            mGraphicsLayerIdentify.addGraphic(graphic);
                                            feature = null;
                                        }
                                        if (featureSet.featureCount() > 0) {
                                            featureSet = null;
                                            j = getdatatables.size() + 2;
                                        }
                                    }
                                }

                                // handle any errors
                                public void onError(Throwable e) {
                                    Log.e("e", "Select Features Error" + e.getLocalizedMessage());
                                }
                            });
                }
                if (j > i) {
                    i = j;
                }
            }
        }
    }

    public void clearSearchLd() {
        mGraphicsLayerIdentify.removeAll();
    }

    private void showSearchLd(List<LocaDate> mLocationList) {
        mGraphicsLayerIdentify.removeAll();
        List<Point> pointList = new ArrayList<>();
        SpatialReference spatialReference1 = SpatialReference.create(4326);
        SpatialReference spatialReference = mMapView.getSpatialReference();
        for (int i = 0; i < mLocationList.size(); i++) {
//            pointList.add(
//                    (Point) GeometryEngine.project(new Point(mLocationList.get(i).lng + 0.0001 * i, mLocationList.get(i).lat + 0.0001 * i)
//                            , spatialReference1
//                            , spatialReference));
            pointList.add(
                    (Point) GeometryEngine.project(new Point(mLocationList.get(i).lng, mLocationList.get(i).lat)
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
        mGraphicsLayerIdentify.addGraphic(graphic);
        mMapView.setExtent(multipath, 100);
    }

    /**
     * Overidden method from Activity class - this is the recommended way of creating dialogs
     */
    private void onCreatePopupWindow(ArrayList data) {
        View listLayout = View.inflate(getActivity(), R.layout.attribute_list_activity, null);
        ListView listView = (ListView) listLayout.findViewById(R.id.listview);
        ImageView back = (ImageView) listLayout.findViewById(R.id.hide_popupWindow);
        AttributeListAdapter myAdapter = new AttributeListAdapter(mContext, data);
        listView.setAdapter(myAdapter);
        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
        int height = windowManager.getDefaultDisplay().getHeight() / 2;
        int width = windowManager.getDefaultDisplay().getWidth();
        PopupWindow popupWindow = new PopupWindow(listLayout, width, height);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupWindow!=null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(mMapView, 0, -height);
    }

    class MyTouchListener extends MapOnTouchListener {

        //此变量是用来保存线或者面的轨迹数据的
        MultiPath poly;

        //判断是执行的哪种绘图方式，它可以为这三种值：point，polgyline，polgyon
        String type = "";

        //绘画点时用于储存点的变量
        Point startPoint = null;

        //构造方法
        public MyTouchListener(Context context, MapView view) {
            super(context, view);
        }

        public void setType(String geometryType) {
            this.type = geometryType;
        }

        public String getType() {
            return this.type;
        }

        //当我们绘制点时执行的处理函数
        public boolean onSingleTap(MotionEvent e) {
            if (type.length() > 1 && type.equalsIgnoreCase(Const.DrawType.POINT)) {
                mGraphicsLayermeasure.removeAll();//绘制点时首先清除图层要素
                //生成点图形，并设置相应的样式
                Graphic graphic = new Graphic(mMapView.toMapPoint(new Point(e.getX(), e
                        .getY())),
                        new SimpleMarkerSymbol(Color.RED, 25, SimpleMarkerSymbol.STYLE.CIRCLE));
                //将点要素添加到图层上
                mGraphicsLayermeasure.addGraphic(graphic);
                //设置按钮可用
                return true;
            } else if (ifidentify) {
                identify(e.getX(), e.getY());
            }
            return false;

        }

        //当绘制线或者面时调用的函数
        public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
            if (type.length() > 1
                    && (type.equalsIgnoreCase(Const.DrawType.POLYLINE) || type
                    .equalsIgnoreCase(Const.DrawType.POLYGON))) {
                //得到移动后的点
                Point mapPt = mMapView.toMapPoint(to.getX(), to.getY());
                //判断startPoint是否为空,如果为空，给startPoint赋值
                if (startPoint == null) {
                    mGraphicsLayermeasure.removeAll();
                    poly = type.equalsIgnoreCase(Const.DrawType.POLYLINE) ? new Polyline() : new Polygon();
                    startPoint = mMapView.toMapPoint(from.getX(), from.getY());
                    //将第一个点存入poly中
                    poly.startPath((float) startPoint.getX(), (float) startPoint.getY());
                    Graphic graphic = new Graphic(startPoint, new SimpleLineSymbol(Color.RED, 5));
                    mGraphicsLayermeasure.addGraphic(graphic);
                }
                //将移动的点放入poly中
                poly.lineTo((float) mapPt.getX(), (float) mapPt.getY());
                Graphic graphic = new Graphic(poly, new SimpleLineSymbol(Color.BLUE, 5));
                mGraphicsLayermeasure.addGraphic(graphic);
                return true;
            }
            return super.onDragPointerMove(from, to);

        }

        //当绘制完线或面，离开屏幕时调用的函数
        @Override
        public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
            if (type.length() > 1 && (type.equalsIgnoreCase(Const.DrawType.POLYLINE) || type
                    .equalsIgnoreCase(Const.DrawType.POLYGON))) {
                //判断当绘制的是面时，将起始点填入到poly中形成闭合
                if (type.equalsIgnoreCase(Const.DrawType.POLYGON)) {
                    poly.lineTo((float) startPoint.getX(),
                            (float) startPoint.getY());
                    mGraphicsLayermeasure.removeAll();
                    mGraphicsLayermeasure
                            .addGraphic(new Graphic(poly, new SimpleFillSymbol(Color.RED)));
                }
                //最后将poly图形添加到图层中去
                Graphic graphic = new Graphic(poly, new SimpleLineSymbol(Color.BLUE, 5));
                mGraphicsLayermeasure.addGraphic(graphic);
                startPoint = null;
                Polyline polyline = (Polyline) graphic.getGeometry();
                BigDecimal db = new BigDecimal(polyline.calculateLength2D());
                int polylinelength = db.intValue();
                ToastUtils.showToast("长度：" + polylinelength + "米");
                //3秒后清除测量划线
                handler.sendEmptyMessageDelayed(0, 3000);
                myListener.setType("");
                return true;
            }
            return super.onDragPointerUp(from, to);
        }
    }

    public void loadMap(String filename, String maptype, String servertype)
            throws FileNotFoundException {
        switch (maptype) {
            case "bg":
                loadMap_bg(filename);
                break;
            case "gl":
                loadMap_gl(filename);
                break;
            case "online":
                loadMap_online(filename, servertype);
                break;
        }
    }

    public void loadMap_gl(String filename) throws FileNotFoundException {
        Layer[] layers = mMapView.getLayers();
        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            if (!"背景底图".equals(layer.getName())) {
                mMapView.removeLayer(layer);
            }
        }
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
        File demoDataFile = Environment.getExternalStorageDirectory();
        String offlineDataSDCardDirName = this.getResources().getString(R.string.offline_dir_gl);
        // create the path
        String basemap = demoDataFile + File.separator + offlineDataSDCardDirName + File.separator
                + filename;
        //Open the geodatabase file
        geodatabase = new Geodatabase(basemap);
        List getdatatables = geodatabase.getGeodatabaseTables();

        for (int i = 0; i < getdatatables.size(); i++) {
            //create a feature layer
            GeodatabaseFeatureTable geodatabaseFeatureTable
                    = (GeodatabaseFeatureTable) getdatatables.get(i);
            FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);
            // add the layer
            mMapView.addLayer(featureLayer);
        }

        if (getdatatables.size() > 0) {
            FeatureLayer featureLayer1 = new FeatureLayer(
                    (GeodatabaseFeatureTable) getdatatables.get(0));
            mMapView.setExtent(featureLayer1.getFullExtent());
        }
        mMapView.addLayer(mGraphicsLayerIdentify);
        mMapView.addLayer(mGraphicsLayermeasure);
        // enable panning over date line
        mMapView.enableWrapAround(true);
        filepath = basemap;
        //加载地图后发出广播
        RestricMsg.get().post("AttrFragment", "");
    }

    public void loadMap_bg(String filename) {
        mMapView.removeAll();
        Log.d("init loadMap", "init loadMap");
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
        File demoDataFile = Environment.getExternalStorageDirectory();
        String offlineDataSDCardDirName = this.getResources().getString(R.string.offline_dir_bg);
        // create the url
        String basemap = demoDataFile + File.separator + offlineDataSDCardDirName + File.separator
                + filename;
        // create the local tpk
        ArcGISLocalTiledLayer localTiledLayer = new ArcGISLocalTiledLayer(basemap);
        localTiledLayer.setName("背景底图");
        // add the layer
        mMapView.addLayer(localTiledLayer);
        mMapView.setExtent(localTiledLayer.getFullExtent());
        mMapView.addLayer(mGraphicsLayerIdentify);
        mMapView.addLayer(mGraphicsLayermeasure);
        // enable panning over date line
        mMapView.enableWrapAround(true);
        filepath = "";
        //加载地图后发出广播
        RestricMsg.get().post("AttrFragment", "");
    }

    public void loadMap_online(String filename, String servertype) throws FileNotFoundException {
        mMapView.removeAll();
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
        if (servertype.equals("mapserver")) {
            ArcGISTiledMapServiceLayer dynamicLayout = new ArcGISTiledMapServiceLayer(filename);
            dynamicLayout.setName("背景底图");
            mMapView.addLayer(dynamicLayout);
        } else if (servertype.equals("imageserver")) {
            ArcGISTiledMapServiceLayer dynamicLayout = new ArcGISTiledMapServiceLayer(filename);
            dynamicLayout.setName("背景底图");
            mMapView.addLayer(dynamicLayout);
        }
        mMapView.addLayer(mGraphicsLayerIdentify);
        mMapView.addLayer(mGraphicsLayermeasure);
        filepath = "";
        //加载地图后发出广播
        RestricMsg.get().post("AttrFragment", "");
    }

    public void relaodMaplayers(ArrayList arrayList) {
        Layer[] layers = mMapView.getLayers();
        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            layer.setVisible((Boolean) arrayList.get(i));
        }
    }

    public void localMap(Feature feature) {
        Graphic graphic = null;
        Envelope envelope = null;
        if (Const.DrawType.POINT.equals(feature.getGeometry().getType().toString())) {
            graphic = new Graphic(feature.getGeometry(), mRedMarkerSymbol);
            Point pt = (Point) feature.getGeometry();
            envelope = new Envelope(pt, 200, 200);
        } else if (Const.DrawType.POLYLINE.equals(feature.getGeometry().getType().toString())) {
            graphic = new Graphic(feature.getGeometry(), simpleLineSymbol);
            Polyline pl = (Polyline) feature.getGeometry();
            envelope = new Envelope();
            pl.queryEnvelope(envelope);
        }
        mGraphicsLayerIdentify.addGraphic(graphic);
        mMapView.setExtent(envelope, 1000);
    }

    //定位的方法
    public void drawMap_Collect(ArrayList list, String type) {
        Log.e("lat3____", "12122121" + list.size());
        Graphic graphic = null;
        Envelope envelope = null;
        if (Const.DrawType.POINT.equals(type)) {
            if (list.size() > 0) {
                ArrayList templist0 = (ArrayList) list.get(0);
                double lat = (double) templist0.get(0);
                double lng = (double) templist0.get(1);
                Point pt = new Point(lat, lng);
                graphic = new Graphic(pt, mRedMarkerSymbol);
                envelope = new Envelope(pt, 200, 200);
            }
        } else if (Const.DrawType.POLYLINE.equals(type) && list.size() > 1) {

            MultiPath multipath = new Polyline();
            ArrayList templist0 = (ArrayList) list.get(0);
            double lat0 = (double) templist0.get(0);
            double lng0 = (double) templist0.get(1);
            multipath.startPath(lat0, lng0);
            for (int i = 1; i < list.size(); i++) {

                ArrayList templist = (ArrayList) list.get(i);
                lat = (double) templist.get(0);
                lng = (double) templist.get(1);
                Log.e("lat____", "12" + lat);
                multipath.lineTo(lat + 0.1 * i, lng + 0.1 * i);
            }
            graphic = new Graphic(multipath, simpleLineSymbol);
            envelope = new Envelope();
            multipath.queryEnvelope(envelope);
            if (!envelope0.isEmpty()) {
                envelope.merge(envelope0);
            }
            //startindex=list.size()-1;
            envelope0 = envelope;
            Log.e("startindex", "fg" + startindex);
        }
        mGraphicsLayerIdentify.addGraphic(graphic);
        Log.e("getNumberOfGraphics()", mGraphicsLayerIdentify.getNumberOfGraphics() + "--");
        mMapView.setExtent(envelope, 1000);

        // mMapView.centerAndZoom(lat,lng, (float) 0.5);
    }

    List<Point> pointList = null;

    public int getLineLength() {
        Polyline polyline = (Polyline) graphic.getGeometry();
        BigDecimal db = new BigDecimal(polyline.calculateLength2D());
        int polylinelength = db.intValue();
        return polylinelength;
    }

    //drawMap时画的graphic
    Graphic graphic = null;
    Envelope envelope = null;

    public void drawMap(Object obj, String type) {

        if (Const.DrawType.POINT.equals(type)) {
            LocaDate p = (LocaDate) obj;
            if (!p.isworn) {
                double lat = p.lat;
                double lng = p.lng;
                Point pt = new Point(lat, lng);
                graphic = new Graphic(pt, mRedMarkerSymbol);
                envelope = new Envelope(pt, 200, 200);
            }
        } else if (Const.DrawType.POLYLINE.equals(type)) {
            LocaDate newPointData = (LocaDate) obj;
            SpatialReference spatialReference1 = SpatialReference.create(4326);
            SpatialReference spatialReference = mMapView.getSpatialReference();
//            Point newPoint = (Point) GeometryEngine.project(
//                    new Point(newPointData.lng + 0.0001 * i, newPointData.lat + 0.0001 * i)
//                    , spatialReference1
//                    , spatialReference);
            Point newPoint = (Point) GeometryEngine.project(
                    new Point(newPointData.lng, newPointData.lat)
                    , spatialReference1
                    , spatialReference);

            if (multipath == null) {
                multipath = new Polyline();
            }
            if (lastPoint == null) {
                lastPoint = newPoint;
            }
            multipath.startPath(lastPoint);
            multipath.lineTo(newPoint);
            graphic = new Graphic(multipath, simpleLineSymbol);
            envelope = new Envelope();
            multipath.queryEnvelope(envelope);
            if (!envelope0.isEmpty()) {
                envelope.merge(envelope0);
            }
            //startindex=list.size()-1;
            envelope0 = envelope;
            Log.e("startindex", "fg" + startindex);
            lastPoint = newPoint;
            mGraphicsLayerIdentify.addGraphic(graphic);
            Log.e("getNumberOfGraphics()", mGraphicsLayerIdentify.getNumberOfGraphics() + "--");
            mMapView.centerAt(lastPoint, false);
            i++;
            LoggerUtils.d("LayerFragment", i + "");
        }


//            List<LocaDate> list = (List<LocaDate>) obj;
//            if (pointList == null) {
//                pointList = new ArrayList<>();
//            } else {
//                pointList.clear();
//            }

//            SpatialReference spatialReference1 = SpatialReference.create(4326);
//            SpatialReference spatialReference = mMapView.getSpatialReference();
//            for (int i = 0; i < list.size(); i++) {
//                pointList.add(
//                        (Point) GeometryEngine.project(new Point(list.get(i).lng + 0.0001 * i, list.get(i).lat + 0.0001 * i)
//                                , spatialReference1
//                                , spatialReference));
//            }
//
//            Log.e("show list  size", pointList.size() + "");
//            if (pointList.size() > startindex + 1) {
//                MultiPath multipath = new Polyline();
//                multipath.startPath(pointList.get(startindex));
//                for (int i = startindex + 1; i < list.size(); i++) {
//                    multipath.lineTo(pointList.get(i));
//                }
//                graphic = new Graphic(multipath, simpleLineSymbol);
//                envelope = new Envelope();
//                multipath.queryEnvelope(envelope);
//                if (!envelope0.isEmpty()) {
//                    envelope.merge(envelope0);
//                }
//                //startindex=list.size()-1;
//                envelope0 = envelope;
//                Log.e("startindex", "fg" + startindex);
//            }
//        }
//        mGraphicsLayerIdentify.addGraphic(graphic);
//        Log.e("getNumberOfGraphics()", mGraphicsLayerIdentify.getNumberOfGraphics() + "--");
////        if (mMapView != null) {
////            mMapView.setExtent(envelope, 1000);
////        }
//        if (pointList.size() > 1) {
//            mMapView.centerAt(pointList.get(pointList.size() - 1), false);
//        }
////         mMapView.centerAndZoom(lat,lng, (float) 0.5);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_layer;
    }

    /**
     * 获取图层信息，跳转到图层管理页面
     */
    public void toLayersManager() {
        Bundle bundle = new Bundle();
        ArrayList maplayerinfo = new ArrayList();
        double mapscale = mMapView.getScale();
        Layer[] layers = mMapView.getLayers();
        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            ArrayList maplayer = new ArrayList();
            maplayer.add(0, layer.getName());
            maplayer.add(1, "1:" + layer.getMinScale());
            maplayer.add(2, layer.getMinScale());
            maplayer.add(3, layer.isVisible());
            maplayer.add(4, layer.getID());
            maplayerinfo.add(maplayer);
        }
        Intent intent_tuceng = new Intent(mContext, MrLayerActivity.class);
        bundle.putStringArrayList("maplayerinfo", maplayerinfo);
        bundle.putDouble("mapscale", mapscale);
        intent_tuceng.putExtras(bundle);
        CommonUtils.toActivity(mContext, intent_tuceng);
    }

    public void localCenter() {
        mMapView.setScale(50000);//设置地图比例尺
        LocationDisplayManager ls = mMapView.getLocationDisplayManager();
        ls.start();//启动
        LoggerUtils.d("zuobiao", ls.getLocation().toString());
        ls.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);//自动滑动至位置
    }

    @OnClick({R.id.image_tuceng, R.id.image_identify, R.id.image_measure, R.id.image_zoomcenter, R.id.search_btn})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.image_tuceng:
                toLayersManager();
                break;
            case R.id.image_identify:
                mGraphicsLayermeasure.removeAll();
                myListener.setType("");
                if (ifidentify) {
                    ifidentify = false;
                } else {
                    ifidentify = true;
                }
                break;
            case R.id.image_measure:
                ifidentify = false;
                if (myListener.getType().equalsIgnoreCase(Const.DrawType.POLYLINE) || myListener.getType()
                        .equalsIgnoreCase(Const.DrawType.POLYGON) || myListener.getType()
                        .equalsIgnoreCase(Const.DrawType.POINT)) {
                    myListener.setType("");
                } else {
                    myListener.setType(Const.DrawType.POLYLINE);
                }
                break;
            case R.id.image_zoomcenter:
                localCenter();
                break;
            case R.id.search_btn:
                String searchWord = searchContent.getText().toString().trim();
                searchLxList = DbHelperDbHelper.open().getSearchLdList(searchWord, RecordingMedium.getWorkMapId());
                searchResultAdapter.setData(searchLxList);
                if (searchLxList.size() == 0) {
                    ToastUtils.showToast("无记录");
                }
                searchContent.clearFocus();
                mSearchResultLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}

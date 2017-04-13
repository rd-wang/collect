package com.master.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.master.R;
import com.master.app.Constants;
import com.master.app.SynopsisObj;
import com.master.app.inter.CallBack;
import com.master.app.inter.CallBackLocation;
import com.master.app.inter.CommonListener;
import com.master.app.inter.OnClickAnimation;
import com.master.app.inter.Type;
import com.master.app.manager.AcquisitionPara;
import com.master.app.manager.RecordingMedium;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.CommonUtils;
import com.master.app.tools.ContextUtils;
import com.master.app.tools.DataUtils;
import com.master.app.tools.DialogUtils;
import com.master.app.tools.FileManager;
import com.master.app.tools.GPSUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.PreferencesUtils;
import com.master.app.tools.ToastUtils;
import com.master.app.weight.APSTSViewPager;
import com.master.app.weight.AdvancedPagerSlidingTabStrip;
import com.master.bean.Fields;
import com.master.bean.LocaDate;
import com.master.bean.Table;
import com.master.constant.Const;
import com.master.model.MainModelImpl;
import com.master.presenter.MainPresenter;
import com.master.server.LocalServer;
import com.master.ui.adapter.TabsAdapter;
import com.master.ui.fragment.map.LayerFragment;
import com.master.ui.viewer.MainView;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.loader.autohideime.HideIMEUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.master.R.id.lx_info;
import static com.master.app.Constants.OUT_COLLECTION_AUTO_SAVE;
import static com.master.app.Constants.OUT_COLLECTION_AUTO_SAVE_POINT_NUMBER;
import static com.master.ui.fragment.map.LayerFragment.S_LayerFragment;

public class MainActivity extends MvpActivity<MainPresenter>
        implements MainView, CommonListener, CallBackLocation {


    public static MainActivity S_MainActivity;

    public static Table mtable;

    public static String mCJDX = "";

    @BindView(R.id.tabs)
    AdvancedPagerSlidingTabStrip tabs;

    @BindView(R.id.iv_Btn)
    TextView ivBtn;

    @BindView(R.id.btn_ztcj)
    com.rey.material.widget.Button btn_ztcj;

    @BindView(R.id.btn_jxcj)
    com.rey.material.widget.Button btn_jxcj;

    @BindView(R.id.viewPager)
    APSTSViewPager viewPager;

    @BindView(R.id.ll_container)
    LinearLayout llContainer;

    @BindView(R.id.lgd_airpanel)
    NestedScrollView lgdAirpanel;

    @BindView(R.id.main_scroll)
    NestedScrollView mainScroll;

    @BindView(lx_info)
    TextView lxInfo;

    private BottomSheetDialog sheet;

    private List<View> views = new ArrayList<>();

    public static final int DEFUALT_NUM_P_TYPE = 0;

    public static final int DEFUALT_NUM_L_TYPE = 1;

    public static List<Table> lineList = AcquisitionPara.getLineList();

    @BindView(R.id.save)
    Button save;

    private Handler mHandler = new Handler();

    //记录当前显示sheetView类型，0,采集对象面板，1，路线采集面板
    private int mPanelType = DEFUALT_NUM_P_TYPE;

    //记录当前路线编码，保存路段时 路段编码=路线编码+路段序列号 路段编码自动生成。
    //选择采集路线时赋值
    public String currentLXBM;

    //自动保存的点的数量.
    private int autoSavePointCount = 0;

    public long jwdID;

    private BottomSheetBehavior behavior;


    @BindView(R.id.tv_lxfd)
    TextView tv_lxfd;

    String myprovider = LocationManager.NETWORK_PROVIDER;

    LocationListener locationListener = null;


    private BottomSheetBehavior mBehavior1;

    private List<View> list;

    private static boolean mIsbind;
    private RxPermissions mRxPermissions;
    private boolean islxfd = false;
    private boolean settingAutoSave;
    private int settingAutoSavePoints;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(new MainModelImpl());
    }


    @Override
    public void bindView(Bundle savedInstanceState) {
        S_MainActivity = this;
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));
        tabs.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);
        sheet = new BottomSheetDialog(this);
        HideIMEUtil.wrap(this);

        LoggerUtils.d("是否开启常亮", PreferencesUtils.getBoolean(this, Constants.SC_WAKE_LOCK, false) + "");
        if (PreferencesUtils.getBoolean(this, Constants.SC_WAKE_LOCK, false)) {
            CommonUtils.toggleWalkLook(this, true);
        }
        settingAutoSave = PreferencesUtils.getBoolean(this, OUT_COLLECTION_AUTO_SAVE, true);
        settingAutoSavePoints = PreferencesUtils.getInt(this, OUT_COLLECTION_AUTO_SAVE_POINT_NUMBER, 50);


        mRxPermissions = new RxPermissions(this);

        mRxPermissions
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (permission.granted) {

                        } else {
                            //  未获取权限
                            ToastUtils.showToast("您没有授权GPS，请在设置中打开授权");
                        }
                    } else {
                        if (permission.granted) {
                            FileManager.init(this);
                        } else {
                            ToastUtils.showToast("您没有打开写入/读取SD卡的权限，文件夹初始化失败，您可能无法进行地图json导出，无法选取路网背景地图等");
                        }
                    }
                });
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GPSUtils.hasGPSDevice(mContext)) {
            if (!GPSUtils.isOpenGPSsetting(this)) {
                GPSUtils.openGPSSettings(this);
            } else {
                if (!mIsbind) {
                    LoggerUtils.d("Mainactivity", "绑定位置服务");
                    mIsbind = LocalServer.bindServer(this);
                }
            }
        } else {
            showDialog("错误", "设备未检测出GPS定位模块,不能启动位置服务......");
        }

    }

    //显示采集对象选择面板

    /**
     * 100时屏蔽线采集，其他情况正常情况
     */
    @Override
    public void showExtra() {
        View dialog = ContextUtils.inflate(R.layout.mainact_item_dialog);
        sheet.setContentView(dialog);
        if (mPanelType == DEFUALT_NUM_L_TYPE) {
            dialog.findViewById(R.id.warr).setVisibility(View.GONE);
        }
        sheet.show();
    }

    @Override
    public void hideExtra() {
        sheet.dismiss();
    }

    @Override
    public void addFormElements(List<View> views) {
        llContainer.removeAllViews();
        for (View view : views) {
            llContainer.addView(view);
        }
        if ("T_ld".equals(mtable.getTName())) {
            //{路段字段需要单独处理
//            "fName": "LDBM", "fOrder": "1"
//            "fName": "LDMC", "fOrder": "2"
//            "fName": "LDXLH", "fOrder": "3"
//            "fName":"QDJWDID", "fOrder":"4"
//            "fName":"ZDJWDID", "fOrder":"5"
//            "fName":"SSLXBM", "fOrder":"6"
            EditText ldbm = (EditText) views.get(0);
            ldbm.setFocusable(false);
            EditText ldxlh = (EditText) views.get(2);
            ldxlh.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    ldbm.setText(currentLXBM + ldxlh.getText().toString());
                }
            });
            jwdID = DbHelperDbHelper.open().getLastRowId(RecordingMedium.getCurrentJwdName());
            LoggerUtils.d("Mainactivity", "jwdID:" + jwdID + " 起点经纬度：" + (jwdID + 1 - autoSavePointCount) + " 终点经纬度：" + (jwdID + 1 + lat_lng_list.size()));
            ((EditText) views.get(3)).setText(jwdID + 1 - autoSavePointCount + "");
            ((EditText) views.get(4)).setText(jwdID + lat_lng_list.size() + "");
            ((EditText) views.get(5)).setText(currentLXBM);

        }
        this.views.clear();
        this.views.addAll(views);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public List<Fields> getArguments(String fname, List<Fields> list) {
        return presenter.getFieldsParam(fname, list);
    }


    private void animateClickView(View v, OnClickAnimation callback) {
        float factor = 1;
        ViewPropertyAnimator.animate(v).scaleX(factor).scaleY(factor).alpha(0.5f);
    }


    public void setPanelType(int panelType) {
        mPanelType = panelType;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sInfo = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        String sInfo = "noll thing！";
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String Type = (String) buttonView.getTag(R.id.key);
    }

    public void changetabs(int item) {
        viewPager.setCurrentItem(item, false);
    }


    //根据字段生产view
    public void builderSheet(List<Fields> fieldsList) {
        list = presenter.builderSheetElement(this, fieldsList);
    }

    public void show(View v) {
        LoggerUtils.d(sClassName, "show");
        behavior = BottomSheetBehavior.from(v);
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void hideKeyBoard() {
        super.hideSoftKeyboard();
    }


    //开始采集面板
    @Override
    public void showAirPanel() {
        mBehavior1 = BottomSheetBehavior.from(lgdAirpanel);
        //采集状态禁用
        lgdAirpanel.setNestedScrollingEnabled(false);
        if (mBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }

    //开始采集面板
    public void hintAirPanel() {
        mBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    public void showDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", null);
        builder.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if (behavior != null && behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            return true;
//        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long mExitTime;

    public void exit() {

        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    @OnClick({R.id.iv_Btn, R.id.tv_lxfd, R.id.btn_dcj, R.id.btn_tzcj, R.id.btn_ztcj, R.id.btn_jxcj,
            R.id.save, R.id.reset, R.id.btn_tccj})
    public void onClick(View view) {
        if (!RecordingMedium.checkWorkMap()) {
            showToast("请先设置工作地图");
            return;
        }
        if (!SynopsisObj.getAppContext().isOpenLocalServer() && !mIsbind) {
            new DialogUtils.Builder(this)
                    .addTitle("提示")
                    .addMsg("未检测到位置服务开启，请确保GPS模块启动，并重启应用")
                    .addCancel("确定")
                    .build()
                    .setCancelable()
                    .showDialog();
            return;
        }
        super.hideSoftKeyboard();
        changetabs(0);
        switch (view.getId()) {
            case R.id.iv_Btn:
                if (mPanelType == DEFUALT_NUM_L_TYPE) {
                    showAirPanel();
                } else if (mPanelType == DEFUALT_NUM_P_TYPE) {
                    showExtra();
                }
                break;
            case R.id.tv_lxfd:
                islxfd = true;
                LocalServer.getServer().pause();
                if (DataUtils.isNotEmpty(lineList)) {
                    mtable = lineList.get(0);
                }
                List<Fields> t_ld = getArguments(mtable.getTName(), new ArrayList<>());
                builderSheet(t_ld);
                show(findViewById(R.id.main_scroll));
                break;
            case R.id.btn_dcj:  //点采集
                showExtra();
                break;
            case R.id.btn_tzcj: //停止采集 -- 暂停+分段
                LocalServer.getServer().pause();
                btn_ztcj.setEnabled(false);
                btn_jxcj.setEnabled(true);
                islxfd = true;
                mPanelType = DEFUALT_NUM_P_TYPE;
                if (DataUtils.isNotEmpty(lineList)) {
                    mtable = lineList.get(0);
                }
                List<Fields> t_lds = getArguments(mtable.getTName(), new ArrayList<>());
                builderSheet(t_lds);
                show(findViewById(R.id.main_scroll));
//                hintAirPanel();
                break;
            case R.id.btn_ztcj:
                //暂停
                btn_ztcj.setEnabled(false);
                btn_jxcj.setEnabled(true);
                LocalServer.getServer().pause();
                ToastUtils.showToast("采集已暂停");
                break;
            case R.id.btn_jxcj:
                btn_ztcj.setEnabled(true);
                btn_jxcj.setEnabled(false);
                LocalServer.getServer().resume();
                ToastUtils.showToast("继续采集");
                break;
            case R.id.btn_tccj:
                new DialogUtils.Builder(this)
                        .addMsg("确认退出采集吗？")
                        .addSubmittext("确定").addCancel("取消")
                        .build()
                        .initSubmit(new CallBack() {
                            @Override
                            public void call(DialogInterface dialog) {
                                LocalServer.getServer().stop();
                                mPanelType = DEFUALT_NUM_P_TYPE;
                                resetCollectData();
                                hintAirPanel();
                                LayerFragment.S_LayerFragment.hideSearchAndButton(false);
                                dialog.dismiss();
                            }
                        }).showDialog();
                break;
            case R.id.reset:
                if (behavior != null && behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (!islxfd) {
                        resetCollectData();
                    } else {
                        LocalServer.getServer().resume();
                    }
                }
                break;
            case R.id.save:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (presenter.writeValue(list, Type.SAVE)) {
                    if (!islxfd) {
                        resetCollectData();
                    } else {
                        clearLocalData();
                        LocalServer.getServer().resume();
                    }
                }
                break;
        }
    }

    /**
     * 清空图层内容 清除记录的数据 --点，路径，自动保存计算起点jwdid，以及jwd集合。
     */
    private void resetCollectData() {
        LayerFragment s_layerFragment = LayerFragment.S_LayerFragment;
        GraphicsLayer mGraphicsLayerIdentify = s_layerFragment.mGraphicsLayerIdentify;
        if (mGraphicsLayerIdentify != null) {
            mGraphicsLayerIdentify.removeAll();
        }
        s_layerFragment.lastPoint = null;
        s_layerFragment.multipath = null;
        clearLocalData();
        s_layerFragment.i = 0;
    }

    private void clearLocalData() {
        autoSavePointCount = 0;
        lat_lng_list.clear();
    }

    public List<LocaDate> lat_lng_list = new ArrayList();


    public void startLocation() {
        LocalServer.getServer().start(this, this);
        ToastUtils.showToast("开始采集");
    }

    @Override
    public void call(List<LocaDate> locaDates, LocaDate locaDte) {
        lat_lng_list = locaDates;
        S_LayerFragment.drawMap(locaDte, Const.DrawType.POLYLINE);
        lxInfo.setText(currentLXBM + "  " + S_LayerFragment.getLineLength() + "米");
        if(settingAutoSave){
            if (lat_lng_list.size() >= settingAutoSavePoints) {
                //每？个点save一次
                LocalServer.getServer().pause();
                if (DbHelperDbHelper.open().addPointList(locaDates, RecordingMedium.getCurrentJwdName(), currentLXBM)) {
                    autoSavePointCount += lat_lng_list.size();
                    lat_lng_list.clear();
                    showToast("自动保存");
                }
                LocalServer.getServer().resume();
            }
        }

        LoggerUtils.d("MainActivity call", "locaDates.size():" + locaDates.size() + " lat_lng_list.size():" + lat_lng_list.size());
    }

    public MainPresenter getPresenter() {
        return presenter;
    }


    @Override
    protected void onDestroy() {
        LocalServer.closeLocalServer();
        super.onDestroy();
    }

}
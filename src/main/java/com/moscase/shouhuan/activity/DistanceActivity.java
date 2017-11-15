//package com.moscase.shouhuan.activity;
//
//
//import android.app.Activity;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//import android.widget.ZoomControls;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.BitmapDescriptor;
//import com.baidu.mapapi.map.BitmapDescriptorFactory;
//import com.baidu.mapapi.map.MapPoi;
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MarkerOptions;
//import com.baidu.mapapi.map.MyLocationConfiguration;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.map.OverlayOptions;
//import com.baidu.mapapi.map.Polyline;
//import com.baidu.mapapi.map.PolylineOptions;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.utils.DistanceUtil;
//import com.moscase.shouhuan.R;
//import com.moscase.shouhuan.view.LoadToast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.moscase.shouhuan.R.id.mapView;
//
/**
 * Created by 陈航 on 2017/8/10.
 *
 * 这是展示用户当前运动轨迹的界面，这里我直接调用的是百度地图的API
 * IOS那边用的是高德
 *
 *
 *
 * 2017/10/31
 * 由于小胡经理说项目结构要调整，然后这个功能就不需要了，我就全部注释掉了
 * 但我没有删除，下面这几百行代码就留着吧，搞不好以后又要开启这个任务那就懵逼了
 *
 *
 * 此模块已废弃
 */
//public class DistanceActivity extends Activity implements SensorEventListener {
//
//    // 定位相关
//    LocationClient mLocClient;
//    public MyLocationListenner myListener;
//    private MyLocationConfiguration.LocationMode mCurrentMode;
//    private SensorManager mSensorManager;
//    private Double lastX = 0.0;
//    private int mCurrentDirection = 0;
//    private double mCurrentLat = 0.0;
//    private double mCurrentLon = 0.0;
//    private float mCurrentAccracy;
//
//    private MapView mMapView;
//    private BaiduMap mBaiduMap;
//
//    float mCurrentZoom = 18f;//默认地图缩放比例值
//
//    // UI相关
//    boolean isFirstLoc = true; // 是否首次定位
//    private MyLocationData locData;
//    private LoadToast mLoadToast;
//
//    //起点图标
//    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable
//            .ic_me_history_startpoint);
//    //终点图标
//    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable
//            .ic_me_history_finishpoint);
//
//    List<LatLng> points = new ArrayList<LatLng>();//位置点集合
//    Polyline mPolyline;//运动轨迹图层
//    LatLng last = new LatLng(0, 0);//上一个定位点
//    MapStatus.Builder builder;
//    private LocationClientOption mOption;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_distance);
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
//        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
//        myListener = new MyLocationListenner();
//        Log.d("koma", "初始化了listener");
//        mLoadToast = new LoadToast(this);
//        mLoadToast.setTextColor(getResources().getColor(R.color.colorAccent));
//        mLoadToast.setText("GPS定位中...");
//        mLoadToast.setTranslationY(50);
//
//        // 地图初始化
//        mMapView = (MapView) findViewById(mapView);
//        mBaiduMap = mMapView.getMap();
//        // 开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);
//        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
//                com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
//
//        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                //点击地图某个位置获取经纬度latLng.latitude、latLng.longitude
//            }
//
//            @Override
//            public boolean onMapPoiClick(MapPoi mapPoi) {
//                //点击地图上的poi图标获取描述信息：mapPoi.getName()，经纬度：mapPoi.getPosition()
//                return false;
//            }
//        });
//
//        /**
//         * 添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
//         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
//         */
//        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
//
//            @Override
//            public void onMapStatusChangeStart(MapStatus arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onMapStatusChangeFinish(MapStatus arg0) {
//                mCurrentZoom = arg0.zoom;
//            }
//
//            @Override
//            public void onMapStatusChange(MapStatus arg0) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//
//        //隐藏百度地图的logo
//        View child = mMapView.getChildAt(1);
//        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
//            child.setVisibility(View.INVISIBLE);
//        }
//
//        // 定位初始化
//        mLocClient = new LocationClient(this);
//
//        mOption = new LocationClientOption();
//        mOption.setOpenGps(true); // 打开gps
//        mOption.setCoorType("bd09ll"); // 设置坐标类型
//        mOption.setScanSpan(1000);
//        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        mLocClient.registerLocationListener(myListener);
////        mOption.setNeedDeviceDirect(true);
//        mLocClient.setLocOption(mOption);
//        mLoadToast.show();
//        mLocClient.start();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!mLoadToast.isSuccess()) {
//                    mLoadToast.error();
//                    Toast.makeText(DistanceActivity.this, "获取位置失败", Toast.LENGTH_LONG)
//                            .show();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    }, 2000);
//                }
//            }
//        }, 20000);
//
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        //每次方向改变，重新给地图设置定位数据，用上一次onReceiveLocation得到的经纬度、精度
//        double x = sensorEvent.values[SensorManager.DATA_X];
//        if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
//            mCurrentDirection = (int) x;
//            locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
//                    // 此处设置获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon)
//                    .build();
//            mBaiduMap.setMyLocationData(locData);
//        }
//        lastX = x;
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
//
//    /**
//     * 定位SDK监听函数
//     */
//    public class MyLocationListenner implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//
//            // map view 销毁后不在处理新接收的位置
//            if (location == null || mMapView == null) {
//                return;
//            }
//            if (location.getLocType() == BDLocation.TypeGpsLocation  ||
//                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
//                    location.getLocType() == BDLocation.TypeCacheLocation)  {
//                Log.d("koma", "" + isFirstLoc);
//                if (isFirstLoc) {//首次定位
//                    Log.d("koma", "" + isFirstLoc);
//                    Log.d("koma", "第一次定位");
//                    Log.d("koma   当前模式", location.getLocType() + "");
//                    Log.d("koma   GPS模式", BDLocation.TypeGpsLocation + "");
//                    Log.d("koma   network模式", BDLocation.TypeNetWorkLocation + "");
//                    Log.d("koma   catch模式", BDLocation.TypeCacheLocation + "");
//                    Log.d("koma   offline模式", BDLocation.TypeOffLineLocation + "");
//
//                    //第一个点很重要，决定了轨迹的效果，gps刚开始返回的一些点精度不高，尽量选一个精度相对较高的起始点
//                    LatLng ll = null;
//
//                    ll = getMostAccuracyLocation(location);
//                    if (ll == null) {
//                        return;
//                    }
//                    isFirstLoc = false;
//                    points.add(ll);//加入集合
//                    last = ll;
//
//                    //显示当前定位点，缩放地图
//                    locateAndZoom(location, ll);
//
//                    //标记起点图层位置
//                    MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
//                    oStart.position(points.getDate(0));// 覆盖物位置点，第一个点为起点
//                    oStart.icon(startBD);// 设置覆盖物图片
//                    mBaiduMap.addOverlay(oStart); // 在地图上添加此图层
//                    mLoadToast.success();
//                    return;//画轨迹最少得2个点，首地定位到这里就可以返回了
//                }
//
//                //从第二个点开始
//                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//                //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为5米才添加到集合中
//                if (DistanceUtil.getDistance(last, ll) < 5) {
//                    return;
//                }
//
//                points.add(ll);//如果要运动完成后画整个轨迹，位置点都在这个集合中
//
//                last = ll;
//
//
//                //显示当前定位点，缩放地图
//                locateAndZoom(location, ll);
//
//                //清除上一次轨迹，避免重叠绘画
//                mMapView.getMap().clear();
//
//                //起始点图层也会被清除，重新绘画
//                MarkerOptions oStart = new MarkerOptions();
//                oStart.position(points.getDate(0));
//                oStart.icon(startBD);
//                mBaiduMap.addOverlay(oStart);
//
//                //将points集合中的点绘制轨迹线条图层，显示在地图上
//                if (points.size() >= 2) {
//                    OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000)
//                            .points(points);
//                    Log.d("koma", points + "");
//                    mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
//                }
//            }
//            mCurrentLat = location.getLatitude();
//            mCurrentLon = location.getLongitude();
//            mCurrentAccracy = location.getRadius();
//            locData = new MyLocationData.Builder().accuracy(location.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection).latitude(location.getLatitude()).longitude
//                            (location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
//            if (isFirstLoc) {
////                isFirstLoc = false;
//                LatLng ll1 = new LatLng(location.getLatitude(), location.getLongitude());
//                MapStatus.Builder builder = new MapStatus.Builder();
//                builder.target(ll1).zoom(18.0f);
//                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//            }
//        }
//
//    }
//
//    @Override
//    protected void onPause() {
//        mMapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        mMapView.onResume();
//        super.onResume();
//        // 为系统的方向传感器注册监听器
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor
//                .TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
//    }
//
//    @Override
//    protected void onStop() {
//        // 取消注册传感器监听
//        mSensorManager.unregisterListener(this);
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        // 退出时销毁定位
//        mLocClient.unRegisterLocationListener(myListener);
//        mLocClient.stop();
//        // 关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
//        mMapView.onDestroy();
//        mMapView = null;
//        super.onDestroy();
//    }
//
//
//    /**
//     * 首次定位很重要，选一个精度相对较高的起始点
//     * 注意：如果一直显示gps信号弱，说明过滤的标准过高了，
//     * 可以将location.getRadius()>60中的过滤半径调大，比如>80，
//     * 并且将连续2个点之间的距离DistanceUtil.getDistance(last, ll ) > 20也调大一点，比如>30，
//     * 这里不是固定死的，可以根据需求调整，如果轨迹刚开始效果不是很好，可以将半径调小，两点之间距离也调小，
//     * gps的精度半径一般是10-50米
//     */
//    private LatLng getMostAccuracyLocation(BDLocation location) {
//
//        if (location.getRadius() > 60) {//gps位置精度大于60米的点直接弃用
//            return null;
//        }
//
//        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//
//        if (DistanceUtil.getDistance(last, ll) > 20) {
//            last = ll;
//            points.clear();//有任意连续两点位置大于20，重新取点
//            return null;
//        }
//        points.add(ll);
//        last = ll;
//        //有2个连续的点之间的距离小于20，认为gps已稳定，以最新的点为起始点
//        if (points.size() >= 2) {
//            points.clear();
//            return ll;
//        }
//        return null;
//    }
//
//    private void locateAndZoom(final BDLocation location, LatLng ll) {
//        mCurrentLat = location.getLatitude();
//        mCurrentLon = location.getLongitude();
//        locData = new MyLocationData.Builder().accuracy(0)
//                // 此处设置获取到的方向信息，顺时针0-360
//                .direction(mCurrentDirection).latitude(location.getLatitude())
//                .longitude(location.getLongitude()).build();
//        mBaiduMap.setMyLocationData(locData);
//        builder = new MapStatus.Builder();
//        builder.target(ll).zoom(mCurrentZoom);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//    }
//
//}

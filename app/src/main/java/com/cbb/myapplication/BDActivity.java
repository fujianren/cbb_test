package com.cbb.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

public class BDActivity extends AppCompatActivity {

    private static final String TAG = "BDActivity";
    private MapView mMapView;   // 控件
    private BaiduMap mBaiduMap;     // 地图属性集合对象
    private Marker marker;      // 自定义的标志物
    private Marker mMarkerD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_bd);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        // 地图对象
        mBaiduMap = mMapView.getMap();

        //普通地图显示
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //卫星地图显示
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        //空白地图显示, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);

        //开启交通图
        mBaiduMap.setTrafficEnabled(true);

        // 设置地图比例尺（最大比例等级，最小比例等级），为什么打印出来的都是22.0？？？
        mBaiduMap.setMaxAndMinZoomLevel(1f, 2f);

        // 在地图上设置标志物
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: " + latLng.latitude + "====" + latLng.longitude);
                drig(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                Log.d(TAG, "onMapPoiClick: " + mapPoi.getPosition());
                return false;
            }
        });
    }


    private void drig(LatLng point){
        //定义Maker坐标点
//        LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        //构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(point)
//                .icon(bitmap);
//        //在地图上添加Marker，并显示
//        mBaiduMap.addOverlay(option);

        OverlayOptions options = new MarkerOptions()
                .position(point)  //设置marker的位置
                .icon(bitmap)  //设置marker图标
                .zIndex(9)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽
        //将marker添加到地图上
        marker = (Marker) (mBaiduMap.addOverlay(options));

        /* 长按可以拖拽自定义的标志物 */
        //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
                //拖拽中
                Log.d(TAG, "onMarkerDrag: 正在拖拽");
            }
            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
                Log.d(TAG, "onMarkerDragEnd: 拖拽结束");
            }
            public void onMarkerDragStart(Marker marker) {
                //开始拖拽
                Log.d(TAG, "onMarkerDragStart: 开始拖拽");
            }
        });

//        animator();

        // 将底图标注设置为隐藏，方法如下：
//        mBaiduMap.showMapPoi(false);




        /* 删除指定的标志物 */
//        marker.remove();
    }

    /* 某个点的自定义标志物做动画 */
    private void animator() {
        // 通过marker的icons设置一组图片，再通过period设置多少帧刷新一次图片资源
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        giflist.add(BitmapDescriptorFactory.fromResource(R.mipmap.icon_home_more));
        giflist.add(BitmapDescriptorFactory.fromResource(R.mipmap.edit_flag));
        // 覆盖物选择对象
//        OverlayOptions ooD = new MarkerOptions()
//                .position(new LatLng(39.963175, 116.400244))
//                .icons(giflist)
//                .zIndex(0).period(10);

        // 标记物选择对象
        MarkerOptions ooD = new MarkerOptions()
                .position(new LatLng(39.963175, 116.400244))
                .icons(giflist)
                .zIndex(0).period(10)
                .alpha(0.5f);       // 透明度
        if (true) {
            // 生长动画，图标会添加一个小--大的动画
            ooD.animateType(MarkerOptions.MarkerAnimateType.grow);
        }
        mBaiduMap.addOverlay(ooD);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}


package com.cesium.android.testdrawmaparea

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import java.util.HashMap

class MainActivity : AppCompatActivity(), View.OnClickListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener, BaiduMap.OnMarkerDragListener {

    //定位相关
    private var mLocationClient: LocationClient? = null
    private var mLocationListener: MyLocationListener? = null
    //是否第一次定位，如果是第一次定位的话要将自己的位置显示在地图 中间
    private var isFirstLocation = true

    //marker 相关
    private var marker: Marker? = null
    internal var markers: MutableList<Marker> = ArrayList()
    //算是map的索引，通过此id 来按顺序取出坐标点
    private val ids = ArrayList<String>()
    //用来存储坐标点
    private val latlngs = HashMap<String, LatLng>()

    private var mInfoWindow: InfoWindow? = null
    //线
    private var mPolyline: Polyline? = null
    //多边形
    private var polygon: Polygon? = null
    //private List<Polygon> polygons = new ArrayList<>();
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var la: Double = 0.toDouble()
    private var lo: Double = 0.toDouble()

    private var size: Int = 0
    //根据别名来存储画好的多边形
    private val polygonMap = HashMap<String, Polygon>()
    //多边形的别名
    private val aliasname = ArrayList<String>()
    //
    private var polygonContainsPoint: Boolean = false
    //用来存储一个点所在的所有的区域
    internal var areas: MutableList<String> = ArrayList()

    private inner class MyLocationListener : BDLocationListener {
        override fun onReceiveLocation(location: BDLocation) {
            Log.e("aaa", "位置：" + location.longitude)
            //将获取的location信息给百度map
            val data = MyLocationData.Builder()
                    .accuracy(location.radius)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.direction)
                    .latitude(location.latitude)
                    .longitude(location.longitude)
                    .build()
            mBaiduMap!!.setMyLocationData(data)
            if (isFirstLocation) {

                val ll = LatLng(location.latitude,
                        location.longitude)
                val builder = MapStatus.Builder()
                builder.target(ll).zoom(15.0f)
                mBaiduMap!!.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))

                isFirstLocation = false
//                showInfo("位置：" + location.addrStr)
            }
        }

    }

    override fun onClick(v: View?) {
        //用来确定多变形
        if (v!!.id == R.id.mBtn) {
            //--------------------------确定多边形的大小和别名-----------------------------

            var l: LatLng? = null
            la = 0.0
            lo = 0.0
            size = ids.size
            if (size <= 2) {
                Toast.makeText(this, "点必须大于2", Toast.LENGTH_SHORT).show()
                return
            }
            for (i in 0 until size) {
                l = latlngs[ids[i]]
                la = la + l!!.latitude
                lo = lo + l.longitude
            }

//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("请输入名字：")
//            val inflate = View.inflate(this, R.layout.dialog_aliasname, null)
//            val edt_alias = inflate.findViewById(R.id.edt_alias)
//            builder.setView(inflate)
//            builder.setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
//                val trim = edt_alias.getText().toString().trim({ it <= ' ' })
//                if (trim == "") {
//                    Toast.makeText(this, "别名不能为空！", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }

            drawPolygon()
            // 添加文字，求出多边形的中心点向中心点添加文字
            val llText = LatLng(la / size, lo / size)
//                val ooText = TextOptions()
//                        .fontSize(24).fontColor(-0xff01).text(trim + "")
//                        .position(llText)
//                baidumap.addOverlay(ooText)
//                polygonMap[trim] = polygon
//                aliasname.add(trim)
            polygon = null
            Log.e("aaa", "多边形有几个：" + polygonMap.size)
            Log.e("aaa", "别名有：" + aliasname.toString())
            for (j in markers.indices) {
                markers[j].remove()
            }
            //polygons.add(polygon);
            //polygon = null;
            latlngs.clear()
            ids.clear()
//            })
//            builder.setNegativeButton("取消") { dialogInterface, i -> }
//            builder.create().show()


        }
//        else if (view.getId() == R.id.btn_clear) {
//            map.getMap().clear()
//            latlngs.clear()
//            ids.clear()
//            markers.clear()
//            areas.clear()
//            //polygons.clear();
//            //用来定位
//        } else if (view.getId() == R.id.location) {
//            //点击定位按钮，返回自己的位置
//            isFirstLocation = true
//            showInfo("返回自己位置")
//        } else if (view.getId() == R.id.btn_check) {
//
//            var name: String? = null
//            var polygon: Polygon? = null
//            areas.clear()
//            for (i in aliasname.indices) {
//                name = aliasname[i]
//                Log.e("aaa", "检查的别名是：$name")
//                polygon = polygonMap[name]
//                val s = polygon!!.points.toString()
//                Log.e("aaa", "sssss---->$s")
//                //判断一个点是否在多边形中
//                polygonContainsPoint = SpatialRelationUtil.isPolygonContainsPoint(polygon!!.points, LatLng(latitude, longitude))
//                if (polygonContainsPoint) {
//                    Toast.makeText(this, "该点在 $name 区域内。", Toast.LENGTH_SHORT).show()
//                    areas.add(name)
//                }
//            }
//            Log.e("aaa", "areas" + areas.toString())
//            if (areas.size > 0) {
//                val message = areas.toString()
//                showDialog("所在的区域有：$message")
//            } else {
//                showDialog("该点不在任何区域内。")
//            }
//
//        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }

    private fun drawPolygon() {
        if (polygon != null) {
            polygon!!.remove()
        }
        var ll: LatLng? = null
        val pts = ArrayList<LatLng>()
        for (i in ids.indices) {
            val s = ids[i]
            Log.e("aaa", "key= " + s + " and value= " + latlngs[s].toString())
            ll = latlngs[s]
            pts.add(ll!!)
        }
        val ooPolygon = PolygonOptions().points(pts)
                .stroke(Stroke(5, -0x55ff0100)).fillColor(-0x55000100)
        val polygonOption = PolygonOptions()
                .points(pts)
                .stroke(Stroke(1, Color.parseColor("#F56161")))
                .fillColor(Color.parseColor("#BFF56161"))
        polygon = mBaiduMap!!.addOverlay(polygonOption) as Polygon
    }

    private fun initLocation() {
        //定位客户端的设置
        mLocationClient = LocationClient(this)
        mLocationListener = MyLocationListener()
        //注册监听
        mLocationClient!!.registerLocationListener(mLocationListener)
        //配置定位
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll")//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setScanSpan(1000)//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true)//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true)//可选，设置是否需要地址描述
        option.setNeedDeviceDirect(false)//可选，设置是否需要设备方向结果
        option.isLocationNotify = false//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true)//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true)//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true)//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false)//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIsNeedAltitude(false)//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        mLocationClient!!.locOption = option
    }

    override fun onMapClick(p0: LatLng) {
//        Toast.makeText(this, "坐标是：" + p0.latitude + ",,," + p0.longitude, Toast.LENGTH_SHORT).show()
        Log.e("aaa", "ditu d zuobiao is -->" + p0.latitude + ",,," + p0.longitude)
        latitude = p0.latitude
        longitude = p0.longitude
        //向地图添加marker
        addMarler(latitude, longitude)
        if (ids.size >= 2) {
            drawLine()
        }
        println("----------------")
        println(PositionUtil.bd09_To_Gps84(p0.latitude, p0.longitude))
    }

    private fun addMarler(latitude: Double, longitude: Double) {
        //定义Maker坐标点
        val point = LatLng(latitude, longitude)
        //构建Marker图标
        val bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.point)
        //构建MarkerOption，用于在地图上添加Marker
        val option = MarkerOptions()
                .position(point)
                .icon(bitmap)
                //.zIndex(9)
                .draggable(true)
        //在地图上添加Marker，并显示
        marker = mBaiduMap!!.addOverlay(option) as Marker
        markers.add(marker!!)
        val id = marker!!.id
        latlngs[id] = LatLng(latitude, longitude)
        ids.add(id)
    }

    private fun drawLine() {
        if (mPolyline != null) {
            mPolyline!!.remove()
        }
        val points = ArrayList<LatLng>()
        var l: LatLng? = null
        for (i in ids.indices) {
            l = latlngs[ids[i]]
            points.add(l!!)
        }
        val ooPolyline = PolylineOptions().width(2)
                .color(-0x55010000).points(points)
        mPolyline = mBaiduMap!!.addOverlay(ooPolyline) as Polyline
    }


    override fun onMapPoiClick(p0: MapPoi?): Boolean {
        return false
    }

    override fun onMarkerDragEnd(p0: Marker?) {
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    //    companion object {
//        var start: LatLng? by NotNUllSingleVar()
//    }
//
    private var mMapView: MapView? = null
    private var mBaiduMap: BaiduMap? = null
    //    private var currLocLatLng: LatLng? = null
//    private var redThreadBaiduLatLng: LatLng? = null
//    private var currentPt: LatLng? = null
//    private var clickPt: LatLng? = null
//    private var mPolyline: Polyline? = null
//    private val points = ArrayList<LatLng>()
//    private val temp = ArrayList<LatLng>()
//    private val temp2 = ArrayList<LatLng>()
//
//    private var flag = false
//
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitializer.initialize(applicationContext)
        setContentView(R.layout.activity_main)
        mMapView = findViewById<MapView>(R.id.bmapView)
        mBaiduMap = mMapView!!.map
        mBtn.setOnClickListener(this)
        initLocation()
        mBaiduMap!!.setOnMarkerClickListener(this)
        //给map设置监听事件，用来拿到点击地图的点的坐标
        mBaiduMap!!.setOnMapClickListener(this)
        //给marker设置拖拽监听事件，用来获取拖拽完成后的坐标
        mBaiduMap!!.setOnMarkerDragListener(this)
//        initListener()
//
//        mBtn.setOnClickListener {
//            flag = false
//        }

    }

    //
//    private fun initListener() {
//        mBaiduMap!!.setOnMapTouchListener(BaiduMap.OnMapTouchListener { })
//
//
//        mBaiduMap!!.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
//            override fun onMapPoiClick(p0: MapPoi?): Boolean {
//                return false
//            }
//
//            /**
//             * 单击地图
//             */
//            override fun onMapClick(point: LatLng) {
//                if (!flag) {
//
//                    currentPt = point
//                    redThreadBaiduLatLng = currentPt
//                    println("currentPt$currentPt")
//                    if (currLocLatLng == null) {//判断上一次获取到的经纬度是否存在
//                        currLocLatLng = redThreadBaiduLatLng//如果没有 本次获取的赋值过去
//                        start = redThreadBaiduLatLng
//                    }
//                    println("AFTER$start")
//                    points.add(currLocLatLng!!)
//                    points.add(redThreadBaiduLatLng!!)
//                    println(points)
////                    if(points.size > 1){
//                        val ooPolyline = PolylineOptions().width(10)
//                                .color(-0x55010000)
//                                .points(points)
//                        mPolyline = mBaiduMap!!.addOverlay(ooPolyline) as Polyline
////                    }
//
////                    val polygonOption = PolygonOptions()
////                            .points(points)
////                            .stroke(Stroke(1, Color.parseColor("#F56161")))
////                            .fillColor(Color.parseColor("#BFF56161"))
////
////                    mBaiduMap!!.addOverlay(polygonOption)
//                    currLocLatLng = redThreadBaiduLatLng//本次获取的赋值过去 以便下条线起点使用
//                    //                updateMapState();
//                }else{
//                    clickPt = point
//                    println(isPolygonContainsPoint(points, clickPt))
//                }
//
//            }
//
//
//        })
//
//        mBaiduMap!!.setOnMapDoubleClickListener { point ->
//            /**
//             * 双击地图
//             */
//            currentPt = point
//            val connect = ArrayList<LatLng>()
//            connect.add(start!!)
//            connect.add(points[points.size - 1])
//            println("connect$connect")
//            val ooPolyline = PolylineOptions().width(10)
//                    .color(-0x55010000).points(connect)
//            mPolyline = mBaiduMap!!.addOverlay(ooPolyline) as Polyline
//            currLocLatLng = redThreadBaiduLatLng//本次获取的赋值过去 以便下条线起点使用
//            //                updateMapState();
//            for (i in 0 until points.size - 1) {
//                temp.add(points[i + 1])
//            }
//
//            for (i in 0 until temp.size step 2) {
//                temp2.add(temp[i])
//            }
//
//            println("0000000$temp")
//
//            println("1111111$temp2")
//
//            val polygonOption = PolygonOptions()
//                    .points(temp2)
//                    .stroke(Stroke(1, Color.parseColor("#F56161")))
//                    .fillColor(Color.parseColor("#BFF56161"))
//
//
//            mBaiduMap!!.addOverlay(polygonOption)
////            mBaiduMap!!.setOnPolylineClickListener {  }
//
//            flag = true
//        }
//
//        /**
//         * 地图状态发生变化
//         */
//        mBaiduMap!!.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
//            override fun onMapStatusChangeStart(status: MapStatus) {
//            }
//
//            override fun onMapStatusChangeStart(status: MapStatus, reason: Int) {
//
//            }
//
//            override fun onMapStatusChangeFinish(status: MapStatus) {
//            }
//
//            override fun onMapStatusChange(status: MapStatus) {
//            }
//        })
//    }
//
    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onStart() {
        super.onStart()
        mBaiduMap!!.setMyLocationEnabled(true)
        if (!mLocationClient!!.isStarted) {
            mLocationClient!!.start()
        }
    }

    override fun onStop() {
        super.onStop()
        mBaiduMap!!.setMyLocationEnabled(false);
        if (mLocationClient!!.isStarted()) {
            mLocationClient!!.stop();
        }
    }
//
//    fun isPolygonContainsPoint(var0: List<LatLng>?, var1: LatLng?): Boolean {
//
//        if (var0 != null && var0.size != 0 && var1 != null) {
//            var var2: Int
//            var2 = 0
//            while (var2 < var0.size) {
//                if (var1.longitude == var0[var2].longitude && var1.latitude == var0[var2].latitude) {
//                    return true
//                }
//                ++var2
//            }
//            var2 = 0
//            val var3 = false
//            var var4: LatLng? = null
//            var var5: LatLng? = null
//            var var6 = 0.0
//            val var8 = var0.size
//            for (var9 in 0 until var8) {
//                var4 = var0[var9]
//                var5 = var0[(var9 + 1) % var8]
//                if (var4.latitude != var5.latitude && var1.latitude >= Math.min(var4.latitude, var5.latitude) && var1.latitude <= Math.max(var4.latitude, var5.latitude)) {
//                    var6 = (var1.latitude - var4.latitude) * (var5.longitude - var4.longitude) / (var5.latitude - var4.latitude) + var4.longitude
//                    if (var6 == var1.longitude) {
//                        return true
//                    }
//                    if (var6 < var1.longitude) {
//                        ++var2
//                    }
//                }
//            }
//            return var2 % 2 == 1
//        } else {
//            return false
//        }
//    }
//
//}
//
//class NotNUllSingleVar<T> : ReadWriteProperty<Any?, T> {
//    private var value: T? = null
//    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
//        return value ?: throw IllegalStateException("还没有被赋值")
//    }
//
//    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
//        this.value = if (this.value == null && value != null) value else throw IllegalStateException("不能设置为null，或已经有了")
//    }
}
package com.cesium.android.testdrawmaparea

import android.app.Application
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MainActivity : AppCompatActivity() {
    companion object {
        var start: LatLng? by NotNUllSingleVar()
    }

    private var mMapView: MapView? = null
    private var mBaiduMap: BaiduMap? = null
    private var currLocLatLng: LatLng? = null
    private var redThreadBaiduLatLng: LatLng? = null
    private var currentPt: LatLng? = null
    private var mPolyline: Polyline? = null
    private val points = ArrayList<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitializer.initialize(applicationContext)
        setContentView(R.layout.activity_main)
        mMapView = findViewById(R.id.bmapView) as MapView
        mBaiduMap = mMapView!!.getMap()
        initListener()

    }

    private fun initListener() {
        mBaiduMap!!.setOnMapTouchListener(BaiduMap.OnMapTouchListener { })


        mBaiduMap!!.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
            override fun onMapPoiClick(p0: MapPoi?): Boolean {
                return false
            }

            /**
             * 单击地图
             */
            override fun onMapClick(point: LatLng) {
                currentPt = point
                redThreadBaiduLatLng = currentPt
                println("currentPt$currentPt")
                if (currLocLatLng == null) {//判断上一次获取到的经纬度是否存在
                    currLocLatLng = redThreadBaiduLatLng//如果没有 本次获取的赋值过去
                    start = redThreadBaiduLatLng
                }
                println("AFTER$start")
                points.add(currLocLatLng!!)
                points.add(redThreadBaiduLatLng!!)
                println(points)
                val ooPolyline = PolylineOptions().width(10)
                        .color(-0x55010000).points(points)
                mPolyline = mBaiduMap!!.addOverlay(ooPolyline) as Polyline
                currLocLatLng = redThreadBaiduLatLng//本次获取的赋值过去 以便下条线起点使用
                //                updateMapState();
            }


        })

        mBaiduMap!!.setOnMapDoubleClickListener(BaiduMap.OnMapDoubleClickListener { point ->
            /**
             * 双击地图
             */
            currentPt = point
            val connect = ArrayList<LatLng>()
            connect.add(start!!)
            connect.add(points[points.size - 1])
            println("connect$connect")
            val ooPolyline = PolylineOptions().width(10)
                    .color(-0x55010000).points(connect)
            mPolyline = mBaiduMap!!.addOverlay(ooPolyline) as Polyline
            currLocLatLng = redThreadBaiduLatLng//本次获取的赋值过去 以便下条线起点使用
            //                updateMapState();
        })

        /**
         * 地图状态发生变化
         */
        mBaiduMap!!.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(status: MapStatus) {
            }

            override fun onMapStatusChangeStart(status: MapStatus, reason: Int) {

            }

            override fun onMapStatusChangeFinish(status: MapStatus) {
            }

            override fun onMapStatusChange(status: MapStatus) {
            }
        })
    }

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
}

class NotNUllSingleVar<T> : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("还没有被赋值")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null && value != null) value else throw IllegalStateException("不能设置为null，或已经有了")
    }
}
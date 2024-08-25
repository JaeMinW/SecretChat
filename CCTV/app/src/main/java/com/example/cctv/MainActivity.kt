package com.example.cctv

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var mMap : GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
//        }
//
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        //JSON파일 읽기
//        val jsonStream = resources.openRawResource(R.raw.cctv)
//        val jsonReader = InputStreamReader(jsonStream)
//        val locationType = object : TypeToken<List<Location>>() {}.type
//        val locations: List<Location> = Gson().fromJson(jsonReader, locationType)
//
//        // 마커 추가
//        locations.forEach { location ->
//            val latLng = LatLng(location.lat.toDouble(), location.lot.toDouble())
//            mMap.addMarker(MarkerOptions().position(latLng).title(location.crdn_brnch_nm))
//        }
//
//        // 첫 번째 위치로 카메라 이동
//        if (locations.isNotEmpty()) {
//            val firstLocation = LatLng(locations[0].lat.toDouble(), locations[0].lot.toDouble())
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15f))
//        }
//    }
//
//    data class Location(
//        val grnds_se: String,
//        val lot: String,
//        val crdn_brnch_nm: String,
//        val fix_cctv_addr: String,
//        val cgg_cd: String,
//        val lat: String
//    )
    }
    }
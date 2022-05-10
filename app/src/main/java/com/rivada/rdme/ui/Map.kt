package com.rivada.rdme.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil
import com.rivada.rdme.R
import com.rivada.rdme.utils.toast
import kotlinx.android.synthetic.main.fragment_maps.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Map : Fragment(), OnMapReadyCallback {
    lateinit var mGoogleMap: GoogleMap
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    private val permissionCode = 101
    var locationList: MutableList<Location>?= null

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
             locationList = locationResult.locations
            if (locationList!!.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList!!.last()
                Log.i(
                    "MapsActivity",
                    "Location: " + location.latitude + " " + location.longitude
                )
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

                //move map camera
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mLocationRequest = LocationRequest()
        /*mLocationRequest.interval = 120000 // two minute interval
        mLocationRequest.fastestInterval = 120000*/
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                Looper.myLooper()?.let {
                    mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest, mLocationCallback,
                        it
                    )
                }
                mGoogleMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            Looper.myLooper()?.let {
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    it
                )
            }
            mGoogleMap.isMyLocationEnabled = true
        }
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            permissionCode
                        )
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    permissionCode
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        Looper.myLooper()?.let {
                            mFusedLocationClient?.requestLocationUpdates(
                                mLocationRequest,
                                mLocationCallback,
                                it
                            )
                        }
                        mGoogleMap.isMyLocationEnabled = true
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    requireActivity().toast(R.string.denied)
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)
        floatingActionButton.setOnClickListener {
            Snackbar.make(view, R.string.refresh, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                Looper.myLooper()?.let {
                    mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest, mLocationCallback,
                        it
                    )
                }
                mGoogleMap.isMyLocationEnabled = true
            }
        }
    }

    override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }
}
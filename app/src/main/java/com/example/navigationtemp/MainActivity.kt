package com.example.navigationtemp
import DetectLocation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationtemp.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), DetectLocation.UpdateUI {
    val LOCATION_PERMISSION_ID = 1000
//    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    lateinit var locationRequest: LocationRequest
//    lateinit var latitide: String
//    lateinit var longitude: String
//    private var isPermissionGranted: Boolean = false
//    private lateinit var geoLoc: DetectLocation.GetLoc
    private lateinit var detectLocation: DetectLocation

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Starts, with tread ID ${Thread.currentThread().id}")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detectLocation = DetectLocation(this, this)
        binding.getPos.setOnClickListener {
//            location()
            detectLocation.location()
        }
        binding.capture.setOnClickListener {
            startActivity(Intent(this, CameraXPreview::class.java))
        }
        //        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//        geoLoc = GetLoc()

//        location()
    }

    override fun onResume() {
        super.onResume()
//        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_ID -> {
                if(grantResults.size==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    // All permissions granted
                    detectLocation.onLocationGranted()
                }else{
                    detectLocation.onLocationNotGranted()
                }
            }
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        detectLocation.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 899){
//            Log.d(TAG, "Location Services enables by the user")
//            geoLoc.execute(locationRequest)
//        }else{
//            Log.d(TAG, "Location Services not enables by the user")
//        }

    }

    override fun updateText(latitude: String, longitude: String, distance: Double) {
        binding.latitudeText.visibility = View.VISIBLE
        binding.longitudeText.visibility = View.VISIBLE
        binding.latitudeText.text = latitude
        binding.longitudeText.text = longitude
        val dis_m = distance * 1000
        binding.distance.text = "Distance = " + String.format("%.3f", dis_m) + "m"
        if(dis_m > 100){
            binding.status.text = "False"
        }else{
            binding.status.text = "True"
        }
    }

    override fun makeProgressBarVisible() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun makeProgressBarInvisible() {
        binding.progressBar.visibility = View.INVISIBLE
    }




//    private fun checkLocationPermission(): Boolean {
//        isPermissionGranted = false
//        isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        }else{
//            true
//        }
//        return isPermissionGranted
//    }

//    private fun getPermission(){
//
//    }

//    private fun location() {
//        if(checkLocationPermission()){
//            setLocationRequest()
//            getLocation()
//
//        }else{
//            requestLocationPermissions()
//        }
//    }

//    private fun setLocationRequest() {
//        locationRequest = LocationRequest.create().apply {
//            interval = 5
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            fastestInterval = 5
//            numUpdates = 10
//        }
//    }

//    private fun getLocation() {
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//
//        val client: SettingsClient = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//
//        task.addOnSuccessListener { locationSettingsResponse ->
//
//            val new = GetLoc()
//            new.execute(locationRequest)
//
//        }
//        val REQUEST_CHECK_SETTINGS = 899
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException){
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    exception.startResolutionForResult(this,
//                        REQUEST_CHECK_SETTINGS)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
//    }

//    private fun requestLocationPermissions() {
//        ActivityCompat.requestPermissions(this,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//            LOCATION_PERMISSION_ID
//        )
//    }

    //    private fun getLastLocation(){
//        if(checkLocationPermission()){
//            if(isLocationEnabled()){
//                getNewLocation()
//
////                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
////                    val location = task.result
////                    if (location == null){
////                        getNewLocation()
////                    }else{
////                        binding.LongitudeText.text = location.longitude.toString()
////                        binding.latitudeText.text = location.latitude.toString()
////                    }
////
////                }
//            }else{
//                Toast.makeText(this, "Please Enable your location services", Toast.LENGTH_SHORT).show()
//            }
//        }else{
//            getPermission()
//        }
//    }
//    //    @SuppressLint("MissingPermission")
//    @SuppressLint("MissingPermission")
//    private fun getNewLocation(){
//        Log.d(TAG, "getNewLocation: Start")
//        locationRequest = LocationRequest.create().apply {
//            interval = 5
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            fastestInterval = 5
//            numUpdates = 10
//        }

//        fusedLocationProviderClient.requestLocationUpdates(
//            locationRequest, locationCallback, Looper.myLooper()!!
//        )

//        geoLoc.execute(locationRequest)
//    }

//    private val locationCallback = object : LocationCallback(){
//        override fun onLocationResult(locationResult: LocationResult) {
////            val lastLocation = p0.lastLocation
////            binding.LongitudeText.text = lastLocation.longitude.toString()
////            binding.latitudeText.text = lastLocation.latitude.toString()
//            for (location in locationResult.locations){
//                latitide = location.latitude.toString()
//                longitude = location.longitude.toString()
//                binding.LongitudeText.text = longitude as String
//                binding.latitudeText.text = latitide as String
//                binding.LongitudeText.visibility = View.VISIBLE
//                binding.latitudeText.visibility = View.VISIBLE
//            }
//
//        }
//    }
//    private fun calculateDistance(){
//        val destinationLat = finalLatitude/(180/ PI)
//        val destinationLong = finalLongitude/(180/ PI)
//        val currentLat = latitide.toDouble()/(180/ PI)
//        val currentLong = longitude.toDouble()/(180/ PI)
//
//        val dlon = destinationLong - currentLong
//        val dlat = destinationLat - currentLat
//        val a = sin(dlat / 2).pow(2) + cos(currentLat) * cos(destinationLat) * sin(dlon / 2).pow(2.0)
//        val c = 2 * asin(sqrt(a))
//        val r = 6371
//        val distance = (c * r)
//        Toast.makeText(this, "Distance = $distance", Toast.LENGTH_LONG).show()
//    }

//    inner class GetLoc: CoroutineAsyncTask<LocationRequest, Void, Array<String>>(){
//        @SuppressLint("MissingPermission")
//        override fun doInBackground(vararg params: LocationRequest) {
//            Log.d(TAG, "doInBackground: Start with treah ID: ${Thread.currentThread().id}")
//            var lat: String = ""
//            var lon: String = ""
//
//            val locationCallback = object : LocationCallback(){
//                override fun onLocationResult(locationResult: LocationResult) {
//                    Log.d(TAG, "onLocationResult called, thread ID: ${Thread.currentThread().id}")
//
//                val lastLocation = locationResult.lastLocation
//                lat = lastLocation.latitude.toString()
//                lon = lastLocation.longitude.toString()
//                onPostExecute(arrayOf(lat, lon))
////            binding.LongitudeText.text = lastLocation.longitude.toString()
////            binding.latitudeText.text = lastLocation.latitude.toString()
////                    for (location in locationResult.locations){
////                        latitide = location.latitude.toString()
////                        longitude = location.longitude.toString()
////                        binding.LongitudeText.text = longitude as String
////                        binding.latitudeText.text = latitide as String
////                        binding.LongitudeText.visibility = View.VISIBLE
////                        binding.latitudeText.visibility = View.VISIBLE
//
////                    }
//
//                }
//            }
//            thread {
//                fusedLocationProviderClient.requestLocationUpdates(params[0], locationCallback, Looper.getMainLooper())
//            }
//
////            sleep(5001)
//            Log.d(TAG, "doInBackground: returning $lat, $lon")
////            return arrayOf<String>(lat, lon)
//        }
//
//        override fun onPostExecute(result: Array<String>?) {
//            GlobalScope.launch(Dispatchers.Main) {
//                binding.progressBar.visibility = View.INVISIBLE
//                Log.d(TAG, "onPostExecute: starts, result = ${result?.get(0)}, ${result?.get(1)}")
//                Log.d(TAG, "onPostExecute: starts, thread ID: ${Thread.currentThread().id}")
//                latitide = result?.get(0).toString()
//                longitude = result?.get(1).toString()
//                binding.LongitudeText.text = longitude
//                binding.latitudeText.text = latitide
//                binding.LongitudeText.visibility = View.VISIBLE
//                binding.latitudeText.visibility = View.VISIBLE
//                calculateDistance()
//
//            }
//
//        }
//
//        override fun onPreExecute() {
//            binding.progressBar.visibility = View.VISIBLE
//            super.onPreExecute()
//        }
//
//    }


}
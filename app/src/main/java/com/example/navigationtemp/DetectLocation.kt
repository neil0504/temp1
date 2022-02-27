import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.navigationtemp.CoroutineAsyncTask
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.*

private const val TAG = "DetectLocation"
class DetectLocation(private val context: Context, val listener: UpdateUI){
    interface UpdateUI{
        fun updateText(latitude: String, longitude: String, distance: Double)
        fun makeProgressBarVisible()
        fun makeProgressBarInvisible()
    }
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    var locationRequest = LocationRequest.create().apply {
        interval = 5
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fastestInterval = 5
        numUpdates = 1
    }
    val LOCATION_PERMISSION_ID = 1000

//    private val finalLatitude = 18.4414
//    private val finalLongitude = 73.8010
    private val finalLatitude = 18.4636
    private val finalLongitude = 73.8682
    private val getLoc = GetLoc()

    fun location() {
        if(checkLocationPermission()){
            setLocationRequest()
            getLocation()

        }else{
            requestLocationPermissions()
        }
    }

    private fun checkLocationPermission(): Boolean {
//        isPermissionGranted = false
        val isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }else{
            true
        }
        return isPermissionGranted
    }

    private fun setLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 5
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fastestInterval = 5
            numUpdates = 1
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_ID
        )
    }

    private fun getLocation() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->


            getLoc.execute(locationRequest)

        }
        val REQUEST_CHECK_SETTINGS = 899

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(context as Activity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 899){
            Log.d(TAG, "Location Services enables by the user")
            getLoc.execute(locationRequest)
        }else{
            Log.d(TAG, "Location Services not enables by the user")
        }
    }
    private fun calculateDistance(finalLatitude: String, finalLongitude: String, latitude: String, longitude: String): Double {
        val destinationLat = finalLatitude.toDouble()/(180/ PI)
        val destinationLong = finalLongitude.toDouble()/(180/ PI)
        val currentLat = latitude.toDouble()/(180/ PI)
        val currentLong = longitude.toDouble()/(180/ PI)

        val dlon = destinationLong - currentLong
        val dlat = destinationLat - currentLat
        val a = sin(dlat / 2).pow(2) + cos(currentLat) * cos(destinationLat) * sin(dlon / 2).pow(2.0)
        val c = 2 * asin(sqrt(a))
        val r = 6371
        val distance = (c * r)
        Toast.makeText(context, "Distance = $distance", Toast.LENGTH_LONG).show()
        return distance
    }

    inner class GetLoc: CoroutineAsyncTask<LocationRequest, Void, Array<String>>(){
        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg params: LocationRequest) {
            Log.d(TAG, "doInBackground: Start with treah ID: ${Thread.currentThread().id}")
            var lat = ""
            var lon = ""

            val locationCallback = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d(TAG, "onLocationResult called, thread ID: ${Thread.currentThread().id}")

                    val lastLocation = locationResult.lastLocation
                    lat = lastLocation.latitude.toString()
                    lon = lastLocation.longitude.toString()
                    onPostExecute(arrayOf(lat, lon))
//            binding.LongitudeText.text = lastLocation.longitude.toString()
//            binding.latitudeText.text = lastLocation.latitude.toString()
//                    for (location in locationResult.locations){
//                        latitide = location.latitude.toString()
//                        longitude = location.longitude.toString()
//                        binding.LongitudeText.text = longitude as String
//                        binding.latitudeText.text = latitide as String
//                        binding.LongitudeText.visibility = View.VISIBLE
//                        binding.latitudeText.visibility = View.VISIBLE

//                    }

                }
            }

            fusedLocationProviderClient.requestLocationUpdates(params[0], locationCallback, Looper.getMainLooper())


//            sleep(5001)
            Log.d(TAG, "doInBackground: returning $lat, $lon")
//            return arrayOf<String>(lat, lon)
        }

        override fun onPostExecute(result: Array<String>?) {
            GlobalScope.launch(Dispatchers.Main) {
//                val progressBar: ProgressBar
//                progressBar = (context as Activity).findViewById<ProgressBar>(R.id.)
                listener.makeProgressBarInvisible()
//                binding.progressBar.visibility = View.INVISIBLE
                Log.d(TAG, "onPostExecute: starts, result = ${result?.get(0)}, ${result?.get(1)}")
                Log.d(TAG, "onPostExecute: starts, thread ID: ${Thread.currentThread().id}")
                val latitide = result?.get(0).toString()
                val longitude = result?.get(1).toString()

//                binding.LongitudeText.text = longitude
//                binding.latitudeText.text = latitide
//                binding.LongitudeText.visibility = View.VISIBLE
//                binding.latitudeText.visibility = View.VISIBLE
                val distance = calculateDistance(finalLatitude=finalLatitude.toString(),
                    finalLongitude = finalLongitude.toString(),
                    latitude = latitide,
                    longitude = longitude)

                listener.updateText(latitide, longitude, distance)

            }

        }

        override fun onPreExecute() {
            listener.makeProgressBarVisible()
            super.onPreExecute()
        }

    }

//

    fun onLocationGranted(){
        Log.d(TAG, "onRequestPermissionsResult: You have the permission")
        setLocationRequest()
        getLocation()
    }
    fun onLocationNotGranted(){
        Log.d(TAG, "onRequestPermissionsResult: You dont have the permission, therefore displaying dialog")
        // Display Alert Dialog
        val builder = AlertDialog.Builder(context)
        builder.setMessage("You need to allow app to access location to Function. Allow permission from app settings")
        val dialog = builder.create()
        dialog.show()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode){
//            LOCATION_PERMISSION_ID -> {
//                if(grantResults.size==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                    // All permissions granted
////                    isPermissionGranted = true
////                    Log.d(TAG, "onRequestPermissionsResult: You have the permission")
////                    setLocationRequest()
////                    getLocation()
//                }else{
////                    isPermissionGranted = false
////                    Log.d(TAG, "onRequestPermissionsResult: You dont have the permission, therefore displaying dialog")
////                    // Display Alert Dialog
////                    val builder = AlertDialog.Builder(context)
////                    builder.setMessage("You need to allow app to access location to Function. Allow permission from app settings")
////                    val dialog = builder.create()
////                    dialog.show()
//                }
//            }
//        }
//    }

}
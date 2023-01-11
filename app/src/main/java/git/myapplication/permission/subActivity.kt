package git.myapplication.permission

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import git.myapplication.permission.databinding.ActivitySubBinding
import java.io.IOException
import java.util.*

class subActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySubBinding.inflate(layoutInflater)
    }

    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.CAMERA.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //퍼미션 사용 체크가 안되어 있는 경우
                customDialog()
            } else {
                //퍼미션 사용 체크가 되어 있는 경우
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivity(intent)
                Log.d("Permission Check", "Success")

            }
        }

        binding.LOCATION.setOnClickListener {
            // step 1 is check self permisison
            checkLocationPermission()
        }


        binding.LOCATION.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //퍼미션 사용 체크가 안되어 있는 경우
                customDialog()
            } else {
                checkGPS()
            }
        }

        binding.RECORDAUDIO.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                //퍼미션 사용 체크가 안되어 있는 경우
                customDialog()
            } else {
                //퍼미션 사용 체크가 되어 있는 경우
                val intent = Intent(this,audioRecordActivity::class.java)
                startActivity(intent)
            }
        }

    }


    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 5000 // 업데이트 간격 단위(밀리초)
        locationRequest.fastestInterval = 2000 // 가장 빠른 업데이트 간격 단위(밀리초)
        Priority.PRIORITY_HIGH_ACCURACY //정확성


        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(
            applicationContext
        ).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->

            try {
                //when the GPS is on
                val response = task.getResult(
                    ApiException::class.java
                )

                getUserLocation()

            } catch (e: ApiException) {
                // when the GPS is off
                e.printStackTrace()

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // here we send the request for enable the GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(this@subActivity, 200)

                    } catch (sendIntentException: IntentSender.SendIntentException) {

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // when the setting is unavailable

                    }
                }
            }
        }


    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // when permission is already grant
            checkGPS()
        } else {
            //when permission is denied
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }


    }

    private fun getUserLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.getResult()

            if (location != null) {
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    //here set the address
                    val address_location = address[0].getAddressLine(0)
                    val location = address_location.toString()
                    val uri = Uri.parse("geo:0, 0?q=$location")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)

                } catch (e: IOException) {

                }
            }
        }

    }


    private fun customDialog() {
        val customDialog = customDialog(finishApp = { finish() })
        customDialog.show(supportFragmentManager, "CustomDialog")
    }


}
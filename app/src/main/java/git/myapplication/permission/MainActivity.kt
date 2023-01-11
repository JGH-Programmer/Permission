package git.myapplication.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import git.myapplication.permission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.permissionButton.setOnClickListener {
            if (isAllPermissionGranted()) {
                Snackbar.make(binding.root, "All Permission granted", Snackbar.LENGTH_SHORT).show()
                Log.d("Permission", "All Permission granted")
                val nextIntent = Intent(this, subActivity::class.java)
                startActivity(nextIntent)
            }
            else if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Log.d("Permission", "Permission granted")
                val nextIntent = Intent(this, subActivity::class.java)
                startActivity(nextIntent)
            }
            else {
                requestAllPermissions()
                Log.d("Permission", "Permission granted3")
            }
        }

        binding.camera.setOnClickListener {
            val nextIntent = Intent(this, subActivity::class.java)
            startActivity(nextIntent)
        }


    }


    private fun isAllPermissionGranted(): Boolean = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestAllPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    private fun requestDangerousPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_DANGEROUS_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
                Log.d("Permission", "Permission granted2")
                val nextIntent = Intent(this, subActivity::class.java)
                startActivity(nextIntent)
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Snackbar.make(
                        binding.root,
                        "Permission required to use app!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    requestDangerousPermissions()
                } else {
                    Snackbar.make(binding.root, "Permission denied!", Snackbar.LENGTH_SHORT).show()
                    customDialog()
                    // 오픈 셋팅이 아닌 여기서 팝업창을 띄워서 오픈셋팅에 들어가게 하기
                    // 그다음에 선택 퍼미션 하나 만들어서 그건 그냥 못해도 상관없이 통과하도록
                    // 그다음에 서브 엑티비티를 만들어서 카메라 등등 만들어서 거기서 클릭했을때 권한이 있으면 실행
                    // 없으면 권한을 받을수있게 하기
                }
            }
        }
    }

    private fun openSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        }.run(::startActivity)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET
        )
        private val REQUIRED_DANGEROUS_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
        )
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }

    private fun customDialog() {
        val customDialog = customDialog(finishApp = { finish() })
        customDialog.show(supportFragmentManager, "CustomDialog")
    }


}
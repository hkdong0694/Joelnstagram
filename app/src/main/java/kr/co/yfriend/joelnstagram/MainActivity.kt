package kr.co.yfriend.joelnstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.yfriend.joelnstagram.navigation.*

class MainActivity : AppCompatActivity() {

    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setListener()
        permissionCheck()
    }

    /**
     * InitView ( 초기화 ) Method
     */
    private fun initView() {
        // Default Home Fragment
        fragment = DetailViewFragment()
        changeFragment(fragment)
    }


    /**
     * Listener Method
     */
    private fun setListener() {
        bottom_navigation.run {
            // Bottom Navigation View 각 Id 값 Click Listener
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_home -> {
                        // Home
                        fragment = DetailViewFragment()
                    }
                    R.id.action_search -> {
                        // Search
                        fragment = GridFragment()
                    }
                    R.id.action_add_photo -> {
                        // Photo
                        if (ContextCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // 권한 체크 후 화면 실행
                            startActivity(Intent(applicationContext, AddPhotoActivity::class.java))
                        }
                    }
                    R.id.action_favorite_alarm -> {
                        // Alarm
                        fragment = AlarmFragment()
                    }
                    R.id.action_account -> {
                        // User
                        fragment = UserFragment()
                    }
                }
                changeFragment(fragment)
                true
            }
        }
    }

    /**
     * Fragment Change Method
     */
    private fun changeFragment(fragment: Fragment) {
        // Fragment Attach
        supportFragmentManager.beginTransaction().replace(R.id.vg_container, fragment).commit()
    }

    /**
     * 권한 체크 Method
     */
    private fun permissionCheck() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }

}
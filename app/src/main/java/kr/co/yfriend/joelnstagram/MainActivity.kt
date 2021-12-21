package kr.co.yfriend.joelnstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.yfriend.joelnstagram.navigation.AlarmFragment
import kr.co.yfriend.joelnstagram.navigation.DetailViewFragment
import kr.co.yfriend.joelnstagram.navigation.GridFragment
import kr.co.yfriend.joelnstagram.navigation.UserFragment

class MainActivity : AppCompatActivity() {

    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setListener()

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
                when(item.itemId) {
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
    private fun changeFragment(fragment : Fragment) {
        // Fragment Attach
        supportFragmentManager.beginTransaction().replace(R.id.vg_container, fragment).commit()
    }

}
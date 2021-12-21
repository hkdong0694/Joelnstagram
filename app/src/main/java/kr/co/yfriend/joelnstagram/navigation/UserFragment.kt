package kr.co.yfriend.joelnstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.yfriend.joelnstagram.R

/**
 * Joelnstagram
 * Class: UserFragment
 * Created by 한경동 (Joel) on 2021/12/21.
 * Description:
 */
class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
    }

}
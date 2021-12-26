package kr.co.yfriend.joelnstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kr.co.yfriend.joelnstagram.R
import kr.co.yfriend.joelnstagram.adapter.DetailViewAdapter
import kr.co.yfriend.joelnstagram.navigation.model.ContentDTO

/**
 * Joelnstagram
 * Class: DetailViewFragment
 * Created by 한경동 (Joel) on 2021/12/21.
 * Description:
 */
class DetailViewFragment : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private val adapter: DetailViewAdapter by lazy { DetailViewAdapter() }
    
    private var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    private var contentUidList: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        initView(view)
        fbSetData()
        return view
    }

    /**
     * initView ( 초기화 )
     */
    private fun initView(view: View) {
        fireStore = FirebaseFirestore.getInstance()
        view.rv_item.layoutManager = LinearLayoutManager(activity)
        view.rv_item.adapter = adapter
    }

    /**
     * Firebase 에 등록한 데이터 전부 가져오기
     */
    private fun fbSetData() {
        fireStore.collection("images").orderBy("timestamp").addSnapshotListener { value, _ ->
            contentDTOs.clear()
            contentUidList.clear()
            value?.let { querySnapshot ->
                for (snapshot in querySnapshot.documents) {
                    val item = snapshot.toObject(ContentDTO::class.java)
                    if (item != null) {
                        contentDTOs.add(item)
                        contentUidList.add(snapshot.id)
                    }
                }
                adapter.setData(contentDTOs, contentUidList)
            }
        }
    }

}
package kr.co.yfriend.joelnstagram.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.yfriend.joelnstagram.R
import kr.co.yfriend.joelnstagram.navigation.model.ContentDTO

/**
 * Joelnstagram
 * Class: DetailViewAdapter
 * Created by 한경동 (Joel) on 2021/12/23.
 * Description:
 */
class DetailViewAdapter : RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {

    private var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    private var contentUidList: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contentDTOs[position], contentUidList[position])
    }

    override fun getItemCount() = contentDTOs.size


    fun setData(contentList: ArrayList<ContentDTO>, uidList: ArrayList<String>) {
        this.contentDTOs = contentList
        this.contentUidList = uidList;
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivProfile : ImageView = itemView.findViewById(R.id.iv_profile)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val ivDetailItem : ImageView = itemView.findViewById(R.id.iv_detail_item)
        private val ivFavorite : ImageView = itemView.findViewById(R.id.iv_favorite)
        private val ivChat : ImageView = itemView.findViewById(R.id.iv_chat)
        private val tvLike: TextView = itemView.findViewById(R.id.tv_like)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)

        fun bind(item: ContentDTO, uid: String) {

            // UserId
            tvName.text = item.userId

            // Image ( 유저가 올린 사진 )
            Glide.with(itemView.context).load(item.imageUri).into(ivDetailItem)

            // Content
            tvContent.text = item.explain

            // Likes
            tvLike.text = "Likes " + item.favoriteCount

            // Image Profile
            Glide.with(itemView.context).load(item.imageUri).into(ivProfile)
        }
    }


}
package kr.co.yfriend.joelnstagram.navigation.model

/**
 * Joelnstagram
 * Class: ContentDTO
 * Created by 한경동 (Joel) on 2021/12/22.
 * Description:
 */
data class ContentDTO(
    var explain: String? = null,      // 사진 업로드 설명
    var imageUri: String? = null,    // 사진 Uri
    var userName: String? = null,    // 사진 업로드한 유저 이름
    var userId: String? = null,       // 사진 업로드한 유저 Id
    var timestamp: Long? = null,      // 사진 업로드 시간
    var favoriteCount: Int = 0,     // 업로드 사진 좋아요 개수
    var favorites: Map<String, Boolean> = HashMap()
) {
    data class Comment(
        var userName: String? = null,
        var userId: String? = null,
        var timestamp: Long? = null,
        var comment: String? = null
    )
}
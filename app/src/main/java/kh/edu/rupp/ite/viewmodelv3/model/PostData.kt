package kh.edu.rupp.ite.viewmodelv3.model

data class PostData(
    val title : String,
    val content : String
)

data class LikeOrDisLike(
    val forumId : String,
)

data class UpdateLikeOrDisLike(
    val like : Boolean = false,
    val disLike : Boolean = false
)

data class LikeOrDisLikeComment(
    val commentId : String,
)

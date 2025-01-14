package kh.edu.rupp.ite.viewmodelv3.model

data class ContentDetail(
    val title: String,
    val totalAnswer: Int,
    val totalLike: Int,
    val totalDislike: Int,
    val content: String,
    val isSaved : Boolean = false
)

data class UpdateForumModel(
    val title: String,
    val content: String,
)

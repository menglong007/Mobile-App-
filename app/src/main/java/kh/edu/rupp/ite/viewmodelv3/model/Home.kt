package kh.edu.rupp.ite.viewmodelv3.model

data class HomeModel(
    val id: Int,
    val userId: Int,
    val title: String,
    val totalAnswer: Int?,
    val content: String,
    val username: String,
    val created: String
)

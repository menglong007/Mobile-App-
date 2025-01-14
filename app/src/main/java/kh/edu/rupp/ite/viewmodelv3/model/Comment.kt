package kh.edu.rupp.ite.viewmodelv3.model

data class CommentModel(
    val comment : String,
    var totalLike: Int = 0,
    var totalDislike: Int = 0,
    val username : String,
    val id : Number,
    var like : Boolean = false,
    var disLike : Boolean = false,
    val userId : Int,
    val created : String
)

data class InsertComment(
    val comment : String
)

data class UpdateComment(
    val comment : String
)
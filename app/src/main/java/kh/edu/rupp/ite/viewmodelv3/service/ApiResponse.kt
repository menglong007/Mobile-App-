package kh.edu.rupp.ite.viewmodelv3.service

data class ApiResponse<T> (
    val status : String,
    val message : String,
    val data : T?

)

data class ApiObjectResponse<T>(
    val data: T?,
    val error: String?
)

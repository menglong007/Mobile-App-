package kh.edu.rupp.ite.viewmodelv3.model

data class SignUpRequest(
    val email : String,
    val username: String,
    val password: String
)

data class TokenSignUpResponse(
    val token: String
)
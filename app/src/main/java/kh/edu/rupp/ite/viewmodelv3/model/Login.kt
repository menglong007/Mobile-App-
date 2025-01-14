package kh.edu.rupp.ite.viewmodelv3.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class TokenResponse(
    val token: String
)

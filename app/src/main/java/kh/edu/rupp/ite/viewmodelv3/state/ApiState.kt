package kh.edu.rupp.ite.viewmodelv3.state

data class ApiState<T> (
    val state : EState,
    val data : T?
)

enum class EState {
    loading , success,error
}
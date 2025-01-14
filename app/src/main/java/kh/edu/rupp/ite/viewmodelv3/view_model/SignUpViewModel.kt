    package kh.edu.rupp.ite.viewmodelv2.view_model
    
    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
    import kh.edu.rupp.ite.viewmodelv3.model.LoginRequest
    import kh.edu.rupp.ite.viewmodelv3.model.SignUpRequest
    import kh.edu.rupp.ite.viewmodelv3.state.ApiState
    import kh.edu.rupp.ite.viewmodelv3.state.EState
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch
    
    class SignUpViewModel : ViewModel() {
        private val _signUpState = MutableStateFlow<ApiState<String>>(ApiState(EState.loading, null))
        val signUpState: StateFlow<ApiState<String>> get() = _signUpState

        fun signUp(email: String ,username: String, password: String ) {
            val apiService = ApiManager.getApiService()
            _signUpState.value = ApiState(EState.loading, null)

            viewModelScope.launch {
                try {
                    delay(1000)
                    val signUpRequest = SignUpRequest(email ,username, password)
                    val response = apiService.signUpUser(signUpRequest)

                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (!token.isNullOrEmpty()) {
                            _signUpState.value = ApiState(EState.success, token)
                        } else {
                            _signUpState.value = ApiState(EState.error, null)
                        }
                        Log.d("LoginViewModel", "Login success")
                    } else {
                        Log.e("LoginViewModel", "Login failed with response code: ${response.code()}")
                        _signUpState.value = ApiState(EState.error, null)
                    }
                } catch (ex: Exception) {

                    Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
                    _signUpState.value = ApiState(EState.error, null)
                }
            }

        }
    }

    package kh.edu.rupp.ite.viewmodelv2.view_model
    
    import android.util.Log
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
    import kh.edu.rupp.ite.viewmodelv3.model.LoginRequest
    import kh.edu.rupp.ite.viewmodelv3.state.ApiState
    import kh.edu.rupp.ite.viewmodelv3.state.EState
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch
    
    class LoginViewModel : ViewModel() {
        private val _loginState = MutableStateFlow<ApiState<String>>(ApiState(EState.loading, null))
        val loginState: StateFlow<ApiState<String>> get() = _loginState

        fun login(username: String, password: String) {
            val apiService = ApiManager.getApiService()
            _loginState.value = ApiState(EState.loading, null)

            viewModelScope.launch {
                try {
                    delay(1000)
                    val loginRequest = LoginRequest(username, password)
                    val response = apiService.loginUser(loginRequest)

                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (!token.isNullOrEmpty()) {
                            _loginState.value = ApiState(EState.success, token)
                        } else {
                            _loginState.value = ApiState(EState.error, null)
                        }
                        Log.d("", "Login success")
                    } else {
                        Log.e("LoginViewModel", "Login failed with response code: ${response.code()}")
                        _loginState.value = ApiState(EState.error, null)
                    }
                } catch (ex: Exception) {

                    Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
                    _loginState.value = ApiState(EState.error, null)
                }
            }

        }
    }

package kh.edu.rupp.ite.viewmodelv2.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
import kh.edu.rupp.ite.viewmodelv3.model.HomeModel
import kh.edu.rupp.ite.viewmodelv3.state.ApiState
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val _homeState = MutableLiveData<ApiState<List<HomeModel>>>()
    val homeState: LiveData<ApiState<List<HomeModel>>> get() = _homeState

    fun loadData(filter: String, sort: String, descending: Boolean) {
        val apiService = ApiManager.getApiService()
        _homeState.postValue(ApiState(EState.loading, null))
        viewModelScope.launch {
            try {
                delay(1000)
                val response = apiService.loadDataHome(filter , sort , descending)
                if (response.status == "success") {
                    _homeState.postValue(ApiState(EState.success, response.data))
                    Log.d("statusIsSuccess" ,"Api is response; ${response.data}")
                } else {
                    _homeState.postValue(ApiState(EState.error, null))
                    Log.d("statusIsSuccess" ,"Api is error")
                }
            } catch (ex: Exception) {
                _homeState.postValue(ApiState(EState.error, null))
            }
        }
    }
}
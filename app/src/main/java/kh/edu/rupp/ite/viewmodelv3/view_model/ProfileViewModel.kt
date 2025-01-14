package kh.edu.rupp.ite.viewmodelv2.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
import kh.edu.rupp.ite.viewmodelv2.model.ProfileModel
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.state.ApiState
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profileState = MutableLiveData<ApiState<ProfileModel>>()

    fun loadProfile(context: Context) {
        val apiService = ApiManager.getApiService()
        _profileState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val response = apiService.loadProfile()
                Log.d("ApiProfileRes", "Received profile data: ${response}")
                _profileState.value = ApiState(EState.success, response)
                SharedPreferencesHelper.saveProfile(context, response)
            } catch (ex: Exception) {
                Log.e("ApiProfileRes", "Error loading profile: ${ex.message}")
                _profileState.value = ApiState(EState.error, null)
            }
        }
    }

}

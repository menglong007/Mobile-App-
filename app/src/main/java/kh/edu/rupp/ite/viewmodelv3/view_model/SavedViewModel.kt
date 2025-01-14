package kh.edu.rupp.ite.viewmodelv2.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.model.SavedModel
import kh.edu.rupp.ite.viewmodelv3.model.UpdateComment
import kh.edu.rupp.ite.viewmodelv3.state.ApiState
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {

    private val _savedState = MutableLiveData<ApiState<List<SavedModel>>>()
    val savedState: LiveData<ApiState<List<SavedModel>>> get() = _savedState
    private var UserId : String = ""

    fun loadData(userId: String) {
        UserId = userId
        val apiService = ApiManager.getApiService()
        _savedState.postValue(ApiState(EState.loading, null))

        viewModelScope.launch {
            try {
                val response = apiService.loadDataSaved(userId)
                if (response.status == "success") {
                    _savedState.postValue(ApiState(EState.success, response.data))
                } else {
                    _savedState.postValue(ApiState(EState.error, null))
                    Log.e("SaveForumViewModel", "Failed to save forum with status: 400")
                }
            } catch (ex: Exception) {
                _savedState.postValue(ApiState(EState.error, null))
                Log.e("SavedViewModel", "Error loading saved data: ${ex.message}")
            }
        }
    }

    private val _saveForumState = MutableStateFlow<ApiState<Boolean>>(ApiState(EState.loading, null))

    fun onSaveForum(forumId: String ) {
        val apiService = ApiManager.getApiService()
        _saveForumState.value = ApiState(EState.loading, null)
        viewModelScope.launch {
            try {
                val res = apiService.onSave(forumId)
                if (res.isSuccessful){
                    _saveForumState.value = ApiState(EState.success, true)
                    loadData(UserId)
                } else {
                    _saveForumState.value = ApiState(EState.error, false)
                    Log.e("SaveForumViewModel", "Failed to save forum with status: ${res.code()}")
                }
            } catch (ex: Exception) {
                Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
            }
        }
    }
}

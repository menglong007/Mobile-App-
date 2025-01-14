package kh.edu.rupp.ite.viewmodelv2.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
import kh.edu.rupp.ite.viewmodelv3.model.PostData
import kh.edu.rupp.ite.viewmodelv3.model.PostModel
import kh.edu.rupp.ite.viewmodelv3.state.ApiState
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel(){

    private val _postState = MutableLiveData<ApiState<List<PostModel>>>()
    val postState: LiveData<ApiState<List<PostModel>>> get() = _postState
    fun loadData() {
        val apiService = ApiManager.getApiService()
        _postState.postValue(ApiState(EState.loading, null))
        viewModelScope.launch {
            try {
                delay(1000)
                val response = apiService.loadDataPost()
                if (response.status == "success") {
                    _postState.postValue(ApiState(EState.success, response.data))
                } else {
                    _postState.postValue(ApiState(EState.error, null))
                }
            } catch (ex: Exception) {
                _postState.postValue(ApiState(EState.error, null))
            }
        }
    }



    fun deletePost(postId: String) {
        val apiService = ApiManager.getApiService()
        viewModelScope.launch {
            try {
                val response = apiService.deletePost(postId)
                if (response.isSuccessful) {
                    loadData()
                }
            } catch (ex: Exception) {
                Log.e("DeletePost", "Delete failed", ex)
            }
        }
    }

}
package kh.edu.rupp.ite.viewmodelv2.view_model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.viewmodelv2.service.ApiManager
import kh.edu.rupp.ite.viewmodelv2.model.ProfileModel
import kh.edu.rupp.ite.viewmodelv3.activity.MainMenuActivity
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.model.CommentModel
import kh.edu.rupp.ite.viewmodelv3.model.ContentDetail
import kh.edu.rupp.ite.viewmodelv3.model.HomeModel
import kh.edu.rupp.ite.viewmodelv3.model.InsertComment
import kh.edu.rupp.ite.viewmodelv3.model.LikeOrDisLike
import kh.edu.rupp.ite.viewmodelv3.model.LikeOrDisLikeComment
import kh.edu.rupp.ite.viewmodelv3.model.PostData
import kh.edu.rupp.ite.viewmodelv3.model.UpdateComment
import kh.edu.rupp.ite.viewmodelv3.model.UpdateForumModel
import kh.edu.rupp.ite.viewmodelv3.model.UpdateLikeOrDisLike
import kh.edu.rupp.ite.viewmodelv3.state.ApiState
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class DetailViewModel : ViewModel() {

    private val _detailState = MutableLiveData<ApiState<ContentDetail>>()
    val detailState: LiveData<ApiState<ContentDetail>> get() = _detailState

    fun loadDetail(id: String) {
        val apiService = ApiManager.getApiService()
        _detailState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val response = apiService.loadDetail(id)
                Log.d("DetailResponse", "${response}")

                _detailState.value = ApiState(EState.success, response)
            } catch (ex: Exception) {
                _detailState.value = ApiState(EState.error, null)
                Log.e("DetailError", "Error loading detail", ex)
            }
        }
    }

    private val _commentState = MutableLiveData<ApiState<List<CommentModel>>>()
    val commentState: LiveData<ApiState<List<CommentModel>>> get() = _commentState

    private var ForumId : String ="" ;

    fun loadComment(id: String) {
        ForumId = id
        val apiService = ApiManager.getApiService()
        _commentState.postValue(ApiState(EState.loading, null))
        viewModelScope.launch {
            try {
                delay(1000)
                if (id != null){
                    val response = apiService.loadComment(id)
                    if (response.status == "success") {
                        _commentState.postValue(ApiState(EState.success, response.data))
                        Log.d("statusIsSuccess" ,"Api is response; ${response.data}")
                    } else {
                        _commentState.postValue(ApiState(EState.error, null))
                        Log.d("statusIsSuccess" ,"Api is error")
                    }
                }

            } catch (ex: Exception) {
                _commentState.postValue(ApiState(EState.error, null))
            }
        }
    }

    private val _likeForumState = MutableStateFlow<ApiState<LikeOrDisLike>>(ApiState(EState.loading, null))

    fun onLikeForum(forumId: String , userId: String) {
        val apiService = ApiManager.getApiService()
        _likeForumState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val request = LikeOrDisLike(forumId)
                apiService.likeForum(request)
                loadDetail(forumId)
                onLikeForumDisplay(forumId , userId)
            } catch (ex: Exception) {
                Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
            }
        }
    }

    private val _disLikeForumState = MutableStateFlow<ApiState<LikeOrDisLike>>(ApiState(EState.loading, null))

    fun onDislikeForum(forumId: String , userId: String) {
        val apiService = ApiManager.getApiService()
        _disLikeForumState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val request = LikeOrDisLike(forumId)
                apiService.dislikeForum(request)
                loadDetail(forumId)
                onLikeForumDisplay(forumId , userId)
            } catch (ex: Exception) {
                Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
            }
        }
    }


    private val _onUpdateLikeOrDislikeState = MutableLiveData<ApiState<UpdateLikeOrDisLike>>()
    val onUpdateLikeOrDislikeState: LiveData<ApiState<UpdateLikeOrDisLike>> get() = _onUpdateLikeOrDislikeState


    fun onLikeForumDisplay(forumId: String, userId: String) {
        val apiService = ApiManager.getApiService()
        _onUpdateLikeOrDislikeState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val response = apiService.onUpdateLIkeOrDislike(forumId, userId)
                _onUpdateLikeOrDislikeState.value = ApiState(EState.success, response)
            } catch (ex: Exception) {
                Log.e("ForumViewModel", "Failed to update like/dislike: ${ex.message}")
                _onUpdateLikeOrDislikeState.value = ApiState(EState.error, null)
            }
        }
    }


    private val _likeCommentState = MutableStateFlow<ApiState<LikeOrDisLikeComment>>(ApiState(EState.loading, null))

    fun onLikeComment(commentId: String) {
        val apiService = ApiManager.getApiService()
        _likeCommentState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val request = LikeOrDisLikeComment(commentId)
                apiService.likeComment(request)
                loadComment(ForumId)
            } catch (ex: Exception) {
                Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
            }
        }
    }

    private val _dislikeCommentState = MutableStateFlow<ApiState<LikeOrDisLikeComment>>(ApiState(EState.loading, null))

    fun onDislikeComment(commentId: String) {
        val apiService = ApiManager.getApiService()
        _dislikeCommentState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val request = LikeOrDisLikeComment(commentId)
                apiService.dislikeComment(request)
                loadComment(ForumId)
            } catch (ex: Exception) {
                Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
            }
        }
    }

    fun onSaveForum(forumId: String ) {
        val apiService = ApiManager.getApiService()
        viewModelScope.launch {
            try {
                apiService.onSave(forumId)
                loadDetail(forumId)
            } catch (ex: Exception) {
                Log.e("LoginViewModel", "Login failed with exception: ${ex.message}")
            }
        }
    }

    private val _insertCommentState = MutableStateFlow<ApiState<InsertComment>>(ApiState(EState.loading, null))

    fun onInsertComment(forumId: String, comment: String) {
        val apiService = ApiManager.getApiService()
        _insertCommentState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val request = InsertComment(comment)
                val response = apiService.insertComment(forumId, request)

                if (response.isSuccessful) {
                    loadComment(forumId)
                } else {
                    Log.e("InsertComment", "Error: ${response.message()}")
                }
            } catch (ex: Exception) {
                _insertCommentState.value = ApiState(EState.error, null)  // Error state
                Log.e("InsertComment", "Insert comment failed with exception: ${ex.message}")
            }
        }
    }


    fun deleteComment(id: String) {
        val apiService = ApiManager.getApiService()
        viewModelScope.launch {
            try {
                val response = apiService.deleteComment(id)
                if (response.isSuccessful) {
                    loadComment(id)
                }
            } catch (ex: Exception) {
                Log.e("DeletePost", "Delete failed", ex)
            }
        }
    }

    private val _updateCommentState = MutableStateFlow<ApiState<UpdateComment>>(ApiState(EState.loading, null))

    fun onUpdateComment(commentId: String, comment: String) {
        val apiService = ApiManager.getApiService()
        _updateCommentState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                val request = UpdateComment(comment)
                val response = apiService.onUpdateComment(commentId, request)

                if (response.isSuccessful) {
                    loadComment(ForumId)
                } else {
                    Log.e("InsertComment", "Error: ${response.message()}")
                }
            } catch (ex: Exception) {
                _updateCommentState.value = ApiState(EState.error, null)
                Log.e("InsertComment", "Insert comment failed with exception: ${ex.message}")
            }
        }
    }

    fun onInsertForum(title: String, content: String) {
        val apiService = ApiManager.getApiService()

        viewModelScope.launch {
            try {
                delay(1000)
                val postRequest = PostData(title, content)
                apiService.postData(postRequest)
            } catch (ex: CancellationException) {
                Log.e("PostViewModel", "Post job was cancelled.")
            } catch (ex: Exception) {
                Log.e("PostViewModel", "Post failed with exception: ${ex.message}", ex)
            }
        }
    }

    private val _updateForumState = MutableStateFlow<ApiState<UpdateForumModel>>(ApiState(EState.loading, null))

    fun onUpdateForum(Id: String, title: String,content: String) {
        val apiService = ApiManager.getApiService()
        _updateForumState.value = ApiState(EState.loading, null)

        viewModelScope.launch {
            try {
                Log.e("InsertForum", "Error: $Id")
                val request = UpdateForumModel(title , content)
                val response = apiService.onUpdateForum(Id, request)

                if (response.isSuccessful) {
                    loadDetail(Id)
                } else {
                    Log.e("InsertForum", "Error: ${response.message()}")
                }
            } catch (ex: Exception) {
                _updateForumState.value = ApiState(EState.error, null)
                Log.e("InsertForum", "Insert comment failed with exception: ${ex.message}")
            }
        }
    }

}


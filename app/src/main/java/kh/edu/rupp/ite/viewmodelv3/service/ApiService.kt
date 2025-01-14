package kh.edu.rupp.ite.viewmodelv2.service

import kh.edu.rupp.ite.viewmodelv2.model.ProfileModel
import kh.edu.rupp.ite.viewmodelv3.model.CommentModel
import kh.edu.rupp.ite.viewmodelv3.model.ContentDetail
import kh.edu.rupp.ite.viewmodelv3.model.HomeModel
import kh.edu.rupp.ite.viewmodelv3.model.InsertComment
import kh.edu.rupp.ite.viewmodelv3.model.LikeOrDisLike
import kh.edu.rupp.ite.viewmodelv3.model.LikeOrDisLikeComment
import kh.edu.rupp.ite.viewmodelv3.model.LoginRequest
import kh.edu.rupp.ite.viewmodelv3.model.PostData
import kh.edu.rupp.ite.viewmodelv3.model.PostModel
import kh.edu.rupp.ite.viewmodelv3.model.SavedModel
import kh.edu.rupp.ite.viewmodelv3.model.SignUpRequest
import kh.edu.rupp.ite.viewmodelv3.model.TokenResponse
import kh.edu.rupp.ite.viewmodelv3.model.TokenSignUpResponse
import kh.edu.rupp.ite.viewmodelv3.model.UpdateComment
import kh.edu.rupp.ite.viewmodelv3.model.UpdateForumModel
import kh.edu.rupp.ite.viewmodelv3.model.UpdateLikeOrDisLike
import kh.edu.rupp.ite.viewmodelv3.service.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("User/Login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<TokenResponse>

    @POST("User/Register")
    suspend fun signUpUser(@Body loginRequest: SignUpRequest): Response<TokenSignUpResponse>

    @GET("Forum/GetAll?PageSize=1000")
    suspend fun loadDataHome(
        @Query("Filter") filter: String,
        @Query("SortField") sort: String,
        @Query("Descending") descending: Boolean = true
    ): ApiResponse<List<HomeModel>>

    @GET("Forum/GetMyForums")
    suspend fun loadDataPost() : ApiResponse<List<PostModel>>

    @GET("Forum/getById/{id}")
    suspend fun loadDetail(@Path("id") id: String): ContentDetail

    @GET("User")
    suspend fun loadProfile(): ProfileModel

    @GET("Saved/Forum/{userId}")
    suspend fun loadDataSaved(@Path("userId") userId: String): ApiResponse<List<SavedModel>>

    @DELETE("Forum/Delete/{id}")
    suspend fun deletePost(@Path("id") id: String) : Response<Unit>

    @GET("Comment/Forum/{userId}")
    suspend fun loadComment(@Path("userId") userId: String): ApiResponse<List<CommentModel>>

    @POST("Forum/Post")
    suspend fun postData(@Body body: PostData) : Response<Unit>

    @POST("Like/like")
    suspend fun likeForum(@Body body: LikeOrDisLike)

    @POST("Like/dislike")
    suspend fun dislikeForum(@Body body: LikeOrDisLike)

    @POST("React/addLike")
    suspend fun likeComment(@Body body: LikeOrDisLikeComment)

    @PUT("Comment/Forum/put/{id}")
    suspend fun onUpdateComment(@Path("id") commentId: String,@Body body: UpdateComment) : Response<Unit>

    @POST("React/addDislike")
    suspend fun dislikeComment(@Body body: LikeOrDisLikeComment)

    @POST("Saved")
    suspend fun onSave(@Query("forumId") id: String) : Response<Unit>

    @POST("Comment/Forum/{forumId}/post")
    suspend fun insertComment(
        @Path("forumId") forumId: String,
        @Body request: InsertComment
    ): Response<Unit>

    @GET("Like/Forum/{id}")
    suspend fun onUpdateLIkeOrDislike(
        @Path("id") id: String,
        @Query("userId") userId: String
    ): UpdateLikeOrDisLike

    @DELETE("Comment/Forum/Delete/{id}")
    suspend fun deleteComment(@Path("id") id: String): Response<Unit>

    @PUT("Forum/Put")
    suspend fun onUpdateForum(@Query("userId") userId: String, @Body body: UpdateForumModel): Response<Unit>

}
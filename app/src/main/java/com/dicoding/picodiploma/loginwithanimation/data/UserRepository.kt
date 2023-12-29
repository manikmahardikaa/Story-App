package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.response.SignUpResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService, private val storyDatabase: StoryDatabase
) {

    fun register(name: String, email: String, password: String): LiveData<ResultState<SignUpResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val registerResponse = apiService.register(name, email, password)
            if (!registerResponse.error) {
                emit(ResultState.Success(registerResponse))
            } else {
                emit(ResultState.Error(registerResponse.message ?: "Error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error("Registration Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(ResultState.Error("Signal Problem"))
        }
    }

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            if (!loginResponse.error) {
                val user = UserModel(
                    email = email,
                    token = loginResponse.loginResult.token,
                    isLogin = true
                )
                ApiConfig.token = loginResponse.message
                userPreference.saveSession(user)
                emit(ResultState.Success(loginResponse))
            } else {
                emit(ResultState.Error(loginResponse.message))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error("Registration Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(ResultState.Error("Signal Problem"))
        }
    }


    fun getStories(): LiveData<ResultState<List<ListStoryItem>>> = liveData {
        emit(ResultState.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val storyResponse  = response.getStories()
            val story = storyResponse.listStory
            val storyList = story.map { it ->
                ListStoryItem(
                    it.photoUrl,
                    it.createdAt,
                    it.name,
                    it.description,
                    it.lon,
                    it.id,
                    it.lat
                )
            }
            if (!storyResponse.error) {
                emit(ResultState.Success(storyList))
            } else {
                emit(ResultState.Error(storyResponse.message))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error("Loading Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(ResultState.Error("Signal Problem"))
        }
    }

    fun getStoryDetail(id: String): LiveData<ResultState<DetailStoryResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val detailStoryResponse = response.getDetailStory(id)
            if (detailStoryResponse != null) {
                emit(ResultState.Success(detailStoryResponse))
            } else {
                emit(ResultState.Error("error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            Log.d("Detail: ", errorMessage.toString())
            emit(ResultState.Error("Loading Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(ResultState.Error("Signal Problem"))
        }
    }

    fun uploadStory(imageFile: File, description: String, lat: Float? = null, lon: Float? = null) =
        liveData {
            emit(ResultState.Loading)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val latitude = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val longitude = lon?.toString()?.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            Log.d("Repo Loc", "$latitude , $longitude")
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            try {
                val user = runBlocking { userPreference.getSession().first() }
                val response = ApiConfig.getApiService(user.token)
                val successResponse =
                    response.uploadStory(multipartBody, requestBody, latitude, longitude)
                emit(ResultState.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryUploadResponse::class.java)
                emit(ResultState.Error(errorResponse.message))
            }
        }

    fun getStoryWithLocation(): LiveData<ResultState<List<ListStoryItem>>> = liveData {
        emit(ResultState.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val storyWithLocationResponse = response.getStoriesWithLocation()
            val listStoryWithLocation = storyWithLocationResponse.listStory
            if (storyWithLocationResponse != null) {
                emit(ResultState.Success(listStoryWithLocation))
            } else {
                emit(ResultState.Error(storyWithLocationResponse.message))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error(errorMessage))
        } catch (e: Exception) {
            emit(ResultState.Error("Signal Problem"))
        }
    }

    fun getStoriesPaging(): LiveData<PagingData<ListStoryItem>> {
        val user = runBlocking { userPreference.getSession().first() }
        val response = ApiConfig.getApiService(user.token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, response),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, storyDatabase)
            }.also { instance = it }
    }
}

package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailStoryResponse

class DetailStoryViewModel(private val repository: UserRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _story = MutableLiveData<DetailStoryResponse?>()
    val story: LiveData<DetailStoryResponse?> = _story

    companion object {
        private const val TAG = "UserDetailViewModel"
    }

    fun getStoryDetail(id: String) = repository.getStoryDetail(id)

}
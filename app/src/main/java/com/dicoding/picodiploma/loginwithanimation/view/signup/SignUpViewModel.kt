package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch
import java.io.File

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String) = userRepository.register(name, email, password)
}


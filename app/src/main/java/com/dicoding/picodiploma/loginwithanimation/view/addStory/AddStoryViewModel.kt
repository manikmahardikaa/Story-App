package com.dicoding.picodiploma.loginwithanimation.view.addStory

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import java.io.File

class AddStoryViewModel(private val userRepository: UserRepository):ViewModel() {
    fun uploadStory(image: File, description: String) = userRepository.uploadStory(image, description)
}
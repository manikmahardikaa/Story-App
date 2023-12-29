package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import androidx.appcompat.app.AlertDialog
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.ResultState
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailStoryResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.utils.Utils.convertDate

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra(ID)

        viewModel.getStoryDetail(id.toString()).observe(this){result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Error -> {
                        showToast(result.error)
                        showLoading(false)
                        showFailureDialog()
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        val detailResponse = result.data
                        setStoryDetailData(detailResponse)
                    }
                }
            }

        }
    }

    private fun setStoryDetailData(story: DetailStoryResponse) {
        binding.detailPhoto.visibility = View.VISIBLE
        binding.apply {
            Glide.with(detailPhoto.context)
                .load(story.story.photoUrl)
                .into(detailPhoto)
        }

        binding.detailEllipse.visibility = View.VISIBLE

        binding.detailName.visibility = View.VISIBLE
        binding.detailName.text = story.story.name

        binding.detailDescription.visibility = View.VISIBLE
        binding.detailDescription.text = story.story.description

        binding.tvDetailDate.visibility = View.VISIBLE
        val createAt = resources.getString(R.string.create_at)
        binding.tvDetailDate.text = "$createAt ${story.story.createdAt.convertDate()}"
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showSuccessDialog() {
        if (!isFinishing) {
            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage("Registrasi berhasil. Yuk, login dan mulai belajar!")
                setPositiveButton("Lanjut") { _, _ ->
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun showFailureDialog() {
        if (!isFinishing) {
            AlertDialog.Builder(this).apply {
                setTitle("Oops!")
                setMessage("Registrasi gagal. Silakan coba lagi nanti.")
                setPositiveButton("Ok") { _, _ ->
                    finish()
                }
                create()
                show()
            }
        }
    }

    companion object {
        const val ID = ""
    }
}
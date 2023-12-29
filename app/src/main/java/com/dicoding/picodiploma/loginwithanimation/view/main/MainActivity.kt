package com.dicoding.picodiploma.loginwithanimation.view.main


import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.adapter.LoadingStateAdapter
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.addStory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.map.StoryMapActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                getStories()
                val hello = resources.getString(R.string.greeting, user.email)
                binding.tvEmailUser.text = hello
            }
        }

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }



    private fun getStories() {

        val adapter = StoryAdapter(this@MainActivity)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getStoriesPaging.observe(this) {data ->
            if (data != null) {
                adapter.submitData(lifecycle, data)
                showLoading(false)
            }
        }
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

    private fun setupAction(){
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu -> {
                    showDialog()
                    true
                }
                R.id.menu_map-> {
                    val intent = Intent(this@MainActivity, StoryMapActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false

            }
        }
        binding.fab.setOnClickListener {
            Intent(this, AddStoryActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun logout() {
        viewModel.logout()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val title = getString(R.string.logout)
        val confirmation = getString(R.string.logout_confirmation)
        val yes = getString(R.string.yes)
        val no = getString(R.string.no)

        builder.setTitle(title)
        builder.setMessage(confirmation)

        builder.setPositiveButton(yes) { dialogInterface: DialogInterface, i: Int ->
            logout()
            finish()
        }

        builder.setNegativeButton(no) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
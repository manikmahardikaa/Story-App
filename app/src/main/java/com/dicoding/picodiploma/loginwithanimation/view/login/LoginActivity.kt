package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.customeview.EmailEditText
import com.dicoding.picodiploma.loginwithanimation.customeview.PasswordEditText
import com.dicoding.picodiploma.loginwithanimation.data.ResultState
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginButton: Button
    private lateinit var emailEditText: EmailEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        loginButton = binding.loginButton


        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        setupView()
        setupView()

        binding.root.postDelayed({
            playAnimation()
            binding.loginButton.setOnClickListener { login() }
        }, 500)
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

    private fun login() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        viewModel.login(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        val user = UserModel(
                            token = result.data.loginResult.token,
                            email = email,
                        )
                        viewModel.saveSession(user)
                        alertDialogSucces()
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        alertDialogFailed()
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun alertDialogSucces() {
        AlertDialog.Builder(this).apply {
            val title = getString(R.string.congrats)
            val loginSucceed = getString(R.string.login_succeed)
            val next = getString(R.string.next)

            setTitle(title)
            setMessage(loginSucceed)
            setPositiveButton(next) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun alertDialogFailed() {
        AlertDialog.Builder(this).apply {
            val title = getString(R.string.failed)
            val loginSucceed = getString(R.string.login_failed)
            val next = getString(R.string.next)

            setTitle(title)
            setMessage(loginSucceed)
            setPositiveButton(next) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun setMyButtonEnable() {
        val password = passwordEditText.text
        val email = emailEditText.text
        val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()

        loginButton.isEnabled = password != null && password.toString().length >= 8 && email != null && validEmail
        if (!loginButton.isEnabled){
            binding.loginButton.alpha = 0.3f
        } else {
            binding.loginButton.alpha = 1f
        }
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

}
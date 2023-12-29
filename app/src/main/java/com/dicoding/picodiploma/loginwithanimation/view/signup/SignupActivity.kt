package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.customeview.EmailEditText
import com.dicoding.picodiploma.loginwithanimation.customeview.PasswordEditText
import com.dicoding.picodiploma.loginwithanimation.data.ResultState
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var signupButton: Button
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEditText = binding.nameEditText
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        signupButton = binding.signupButton

        nameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                if(nameEditText.text.isEmpty()){
                    nameEditText.error = "Nama tidak boleh kosong!"
                } else {
                    nameEditText.error = null
                }
                setMyButtonEnable()

            }

        })

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

        binding.root.postDelayed({
            playAnimation()
            setupAction()
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

    private fun setMyButtonEnable() {
        val password = passwordEditText.text
        val email = emailEditText.text.toString()
        val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        signupButton.isEnabled = password != null && password.toString().length >= 8 && email != null && validEmail
        if (!signupButton.isEnabled){
            binding.signupButton.alpha = 0.3f
        } else {
            binding.signupButton.alpha = 1f
        }
    }


    private fun setupAction() {
        binding.signupButton.setOnClickListener() {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.registerUser(name, email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showLoading(false)
                            alertDialog()

                        }
                        is ResultState.Error -> {
                            showLoading(false)
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun alertDialog() {
        AlertDialog.Builder(this).apply {
            val title = getString(R.string.congrats)
            val accountCreated = getString(R.string.account_created)
            val next = getString(R.string.next)

            setTitle(title)
            setMessage(accountCreated)
            setPositiveButton(next) { _, _ ->
                finish()
            }
            create()
            show()
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}

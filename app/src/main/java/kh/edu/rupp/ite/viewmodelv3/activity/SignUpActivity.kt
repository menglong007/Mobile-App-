package kh.edu.rupp.ite.viewmodelv3.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kh.edu.rupp.ite.viewmodelv2.view_model.LoginViewModel
import kh.edu.rupp.ite.viewmodelv2.view_model.SignUpViewModel
import kh.edu.rupp.ite.viewmodelv3.databinding.SignUpActivityBinding
import kh.edu.rupp.ite.viewmodelv3.globle.AppEncrypted
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: SignUpActivityBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.onNext.setOnClickListener {
            onNext()
        }

        lifecycleScope.launch {
            signUpViewModel.signUpState.collect { state ->
                when (state.state) {
                    EState.loading -> {
                        Toast.makeText(this@SignUpActivity, "Loading...", Toast.LENGTH_SHORT).show()
                    }
                    EState.success -> {
                        val token = state.data
                        if (!token.isNullOrEmpty()) {
                            saveToken(token)
                            navigateToHome()
                        } else {
                            Toast.makeText(this@SignUpActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                    EState.error -> {
                        Toast.makeText(this@SignUpActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun onNext() {
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        var isValid = true

        if (username.isEmpty()) {
            binding.usernameLayout.error = "Enter username"
            isValid = false
        } else {
            binding.usernameLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailLayout.error = "Enter email "
            isValid = false
        } else if (!email.matches(Regex(emailRegex))) {
            binding.emailLayout.error = "Invalid email format"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        if (isValid) {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("EMAIL", email)
            startActivity(intent)
        }
    }

    private fun saveToken(token: String) {
        AppEncrypted.get().storeToken(this, token)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }

}

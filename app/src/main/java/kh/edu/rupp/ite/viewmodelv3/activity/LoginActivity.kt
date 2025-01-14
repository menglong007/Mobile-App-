package kh.edu.rupp.ite.viewmodelv3.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.viewmodelv2.view_model.LoginViewModel
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.databinding.SignInActivityBinding
import kh.edu.rupp.ite.viewmodelv3.globle.AppEncrypted
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: SignInActivityBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val passwordEditText: TextInputEditText = binding.password
        val showPasswordCheckBox: CheckBox = binding.showPassword

        passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            passwordEditText.inputType = if (isChecked) {
                android.text.InputType.TYPE_CLASS_TEXT
            } else {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }

        binding.onLogin.setOnClickListener {
            onSubmit()
        }

        lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state.state) {
                    EState.loading -> {
                    }
                    EState.success -> {
                        hideLoading()
                        val token = state.data
                        if (!token.isNullOrEmpty()) {
                            saveToken(token)
                            navigateToHome()
                        }
                    }
                    EState.error -> {
                        hideLoading()
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.onCreate.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onSubmit() {
        val password = binding.password.text.toString()
        val username = binding.usernameInput.text.toString()

        var isValid = true

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Enter password"
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        if (username.isEmpty()) {
            binding.usernameInputLayout.error = "Enter username"
            isValid = false
        } else {
            binding.usernameInputLayout.error = null
        }

        if (isValid) {
            clearErrors()
            loginViewModel.login(username, password)
            showLoading()
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

    private fun hideLoading() {
        binding.loadingIndicator.visibility = View.GONE
        binding.onLogin.isEnabled = true // Enable button
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.onLogin.isEnabled = false // Disable button
    }

    private fun clearErrors() {
        binding.usernameInputLayout.error = null
        binding.passwordInputLayout.error = null
    }
}

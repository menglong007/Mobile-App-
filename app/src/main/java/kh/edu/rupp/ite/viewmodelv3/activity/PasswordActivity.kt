package kh.edu.rupp.ite.viewmodelv3.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.viewmodelv2.view_model.LoginViewModel
import kh.edu.rupp.ite.viewmodelv2.view_model.SignUpViewModel
import kh.edu.rupp.ite.viewmodelv3.databinding.SignUpPasswordActivityBinding
import kh.edu.rupp.ite.viewmodelv3.globle.AppEncrypted
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.launch

class PasswordActivity: AppCompatActivity() {

    private lateinit var binding : SignUpPasswordActivityBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpPasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.onLogin.setOnClickListener {
            onLogin()
        }

        val passwordEditText: TextInputEditText = binding.password
        val showPasswordCheckBox: CheckBox = binding.showPassword

        passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT

        showPasswordCheckBox.isChecked = true

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            passwordEditText.inputType = if (isChecked) {
                android.text.InputType.TYPE_CLASS_TEXT
            } else {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }

        lifecycleScope.launch {
            signUpViewModel.signUpState.collect { state ->
                when (state.state) {
                    EState.loading -> {
                    }
                    EState.success -> {
                        hideLoading()
                        val token = state.data
                        if (!token.isNullOrEmpty()) {
                            saveToken(token)
                            navigateToHome()
                        } else {
                            Toast.makeText(this@PasswordActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                    EState.error -> {
                        Toast.makeText(this@PasswordActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    private fun onLogin() {
        val username = intent.getStringExtra("USERNAME").toString()
        val email = intent.getStringExtra("EMAIL").toString()
        val password = binding.password.text.toString()

        if (password.isEmpty()){
            binding.passwordInputLayout.error = "Password cannot be empty"
            return
        }
        clearErrors()
        binding.passwordInputLayout.error = null;
        Log.d("huhuahufi","$username , $email , $password ")
        signUpViewModel.signUp(email = email ,username = username, password = password)
        showLoading()
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
        binding.password.error = null
        binding.passwordInputLayout.error = null
    }
}
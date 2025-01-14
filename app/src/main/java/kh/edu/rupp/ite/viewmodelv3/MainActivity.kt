package kh.edu.rupp.ite.viewmodelv3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.viewmodelv3.activity.LoginActivity
import kh.edu.rupp.ite.viewmodelv3.databinding.ActivityMainBinding
import kh.edu.rupp.ite.viewmodelv3.fragment.HomeFragment
import kh.edu.rupp.ite.viewmodelv3.fragment.ProfileFragment
import kh.edu.rupp.ite.viewmodelv3.fragment.SaveFragment
import kh.edu.rupp.ite.viewmodelv3.globle.AppEncrypted

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = AppEncrypted.get().getToken(this)
        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showFragment(HomeFragment())

        binding.itemMenu.setOnItemSelectedListener { menuItem ->
            handleOnNavigationItemSelected(menuItem)
        }
    }


    private fun handleOnNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.itemHome -> showFragment(HomeFragment())
            R.id.itemPlace -> showFragment(SaveFragment())
            R.id.itemProfile -> showFragment(ProfileFragment())
            else -> return false
        }
        return true
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", null)
    }
}

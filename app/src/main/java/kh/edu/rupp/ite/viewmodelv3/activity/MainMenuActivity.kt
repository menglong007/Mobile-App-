package kh.edu.rupp.ite.viewmodelv3.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.viewmodelv2.view_model.DetailViewModel
import kh.edu.rupp.ite.viewmodelv2.view_model.HomeViewModel
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.databinding.ActivityMainBinding
import kh.edu.rupp.ite.viewmodelv3.databinding.SignInActivityBinding
import kh.edu.rupp.ite.viewmodelv3.fragment.HomeFragment
import kh.edu.rupp.ite.viewmodelv3.fragment.ProfileFragment
import kh.edu.rupp.ite.viewmodelv3.fragment.SaveFragment

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(/* view = */ binding.root)
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
}
package kh.edu.rupp.ite.viewmodelv3.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.noties.markwon.Markwon
import kh.edu.rupp.ite.viewmodelv2.view_model.DetailViewModel
import kh.edu.rupp.ite.viewmodelv2.view_model.PostViewModel
import kh.edu.rupp.ite.viewmodelv3.adapter.CommentAdapter
import kh.edu.rupp.ite.viewmodelv3.databinding.ViewDetailActivityBinding
import kh.edu.rupp.ite.viewmodelv3.databinding.ViewDetailBeforePostActivityBinding
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.model.CommentModel
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.delay

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ViewDetailBeforePostActivityBinding
    private val detailViewModel : DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewDetailBeforePostActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("TITLE")?.takeIf { it.isNotEmpty() } ?: "Untitled"
        val content = intent.getStringExtra("CONTENT")?.takeIf { it.isNotEmpty() } ?: "No content"

        binding.title.text = title

        val markdown = """
            $content
        """.trimIndent()

        val markwon = Markwon.create(this)
        markwon.setMarkdown(binding.content, markdown)



        binding.backItem.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.onPost.setOnClickListener {
            onSubmit()
        }


    }


    private fun onSubmit() {
        val title = intent.getStringExtra("TITLE").toString().trim()
        val content = intent.getStringExtra("CONTENT").toString().trim()
        detailViewModel.onInsertForum(title,content)

        val delayMillis = 1500L // Delay for navigation
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }, delayMillis)

    }

}

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
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.model.CommentModel
import kh.edu.rupp.ite.viewmodelv3.state.EState
import kotlinx.coroutines.delay

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ViewDetailActivityBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private var UserId : String =""
    private var forumId : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("USERNAME") ?: ""
        val title = intent.getStringExtra("TITLE")?.takeIf { it.isNotEmpty() } ?: "Untitled"
        val content = intent.getStringExtra("CONTENT")?.takeIf { it.isNotEmpty() } ?: "No content"

        val userId = SharedPreferencesHelper.getProfile(applicationContext)?.userId.toString()
        UserId = userId
        binding.username.text = username
        binding.title.text = title

        val markdown = """
            $content
        """.trimIndent()

        val markwon = Markwon.create(this)
        markwon.setMarkdown(binding.content, markdown)

        val id = intent.getStringExtra("ID").orEmpty()
        val idForEdit = intent.getStringExtra("IDFOREDIT").toString()
        forumId = id

        binding.apply {
            onUpdate.visibility = View.GONE
            loadInputComment.visibility = View.GONE
            showLike.visibility = View.GONE
        }

        Log.d("PostLogic", "idForEdit: $idForEdit, id: $id")

        when {
            id.isNotEmpty() -> {
                Log.d("PostLogic", "Viewing an existing post with id: $id")
                binding.apply {
                    loadInputComment.visibility = View.VISIBLE
                    showLike.visibility = View.VISIBLE
                }
                detailViewModel.apply {
                    loadDetail(id)
                    loadComment(id)
                    if (userId != null) {
                        onLikeForumDisplay(id, userId) // Load like status
                    }
                }
            }
            else -> {

                binding.apply {
                    onUpdate.visibility = View.VISIBLE
                    onSaveContent.visibility =View.GONE// Show Update button
                }
                binding.onUpdate.setOnClickListener {
                    val title = binding.title.text.toString()
                    val content = binding.content.text.toString()
                    detailViewModel.onUpdateForum(idForEdit, title, content)

                    val delayMillis = 1000L // Delay for navigation
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, MainMenuActivity::class.java)
                        startActivity(intent)
                    }, delayMillis)
                }
            }
        }



        binding.backItem.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.isLike.setOnClickListener {
            detailViewModel.onLikeForum(id , userId)
        }
        binding.isDisLike.setOnClickListener {
            detailViewModel.onDislikeForum(id , userId)
        }

        binding.isLiked.setOnClickListener {
            detailViewModel.onLikeForum(id , userId)
        }
        binding.isDisLiked.setOnClickListener {
            detailViewModel.onDislikeForum(id , userId)
        }

        binding.onSaveContent.setOnClickListener {
            onSave(id)
        }

        binding.onSavedContent.setOnClickListener {
            onSave(id)
        }

        binding.sendComment.setOnClickListener {
            onComment(id)
        }

    }

    private fun onComment(id: String) {
        val comment = binding.inputComment.text.toString().trim()
        if (comment.isEmpty()) return
        detailViewModel.onInsertComment(id, comment)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputComment.windowToken, 0)
        binding.inputComment.text.clear()
    }



    private fun onSave(id: String) {
        detailViewModel.onSaveForum(id)
    }


    override fun onStart() {
        super.onStart()

        detailViewModel.detailState.observe(this) { state ->
            when (state.state) {
                EState.loading -> {
                }

                EState.success -> {
                    state.data?.let { contentDetail ->
                        binding.title.text = contentDetail.title
                        val markdown = """
                            ${contentDetail.content}
                        """.trimIndent()

                        val markwon = Markwon.create(this)
                        markwon.setMarkdown(binding.content, markdown)
                        binding.totalLike.text = contentDetail.totalLike.toString()
                        binding.totalDisLike.text = contentDetail.totalDislike.toString()

                        if (contentDetail.isSaved){
                            binding.onSavedContent.visibility = View.VISIBLE
                            binding.onSaveContent.visibility = View.GONE
                        } else {
                            binding.onSavedContent.visibility = View.GONE
                            binding.onSaveContent.visibility = View.VISIBLE
                        }

                        val profile = SharedPreferencesHelper.getProfile(applicationContext)
                        profile?.let {
                            val Id = it.userId.toString()
                            val userId = intent.getStringExtra("USERID").toString()
                            if (userId == Id) {
                                binding.edit.visibility = View.VISIBLE
                                binding.edit.setOnClickListener {
                                    val intent = Intent(this, FormActivity::class.java)
                                    intent.putExtra("TITLE", contentDetail.title)
                                    intent.putExtra("CONTENT", contentDetail.content)
                                    intent.putExtra("ID", forumId)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }

                EState.error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }


        detailViewModel.onUpdateLikeOrDislikeState.observe(this) { state ->
            when (state.state) {
                EState.loading -> {
                }

                EState.success -> {
                    state.data?.let { contentDetail ->
                        binding.isLike.visibility = if (contentDetail.like) View.GONE else View.VISIBLE
                        binding.isLiked.visibility = if (contentDetail.like) View.VISIBLE else View.GONE

                        binding.isDisLike.visibility = if (contentDetail.disLike) View.GONE else View.VISIBLE
                        binding.isDisLiked.visibility = if (contentDetail.disLike) View.VISIBLE else View.GONE

                    }
                }

                EState.error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }


        detailViewModel.commentState.observe(this) { state ->
            when (state.state) {
                EState.loading -> {
                    showLoading()
                }

                EState.success -> {
                    hideLoading()
                    state.data?.let {
                        setupRecyclerView(it)
                    }
                }

                EState.error -> {
                    hideLoading()
                }
            }
        }

    }


    private lateinit var commentAdapter: CommentAdapter

    private fun setupRecyclerView(items: List<CommentModel>) {
        commentAdapter = CommentAdapter(
            comments = items,
            detailViewModel = detailViewModel,
            userId = UserId,
            context = this@DetailActivity,
            binding = binding
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = commentAdapter
            setHasFixedSize(true)
        }
    }


    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.loadingIndicator.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }
}

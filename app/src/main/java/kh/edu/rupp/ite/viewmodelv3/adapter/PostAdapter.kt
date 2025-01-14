package kh.edu.rupp.ite.viewmodelv3.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.viewmodelv2.view_model.PostViewModel
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.activity.DetailActivity
import kh.edu.rupp.ite.viewmodelv3.databinding.ItemPostBinding
import kh.edu.rupp.ite.viewmodelv3.model.PostModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PostAdapter(private val items: List<PostModel>,
                  private val context: Context,
                  private val postViewModel : PostViewModel
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            postTitle.text = item.title ?: "Untitled"

            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

            val postTime = try {
                ZonedDateTime.parse(item.created, formatter).withZoneSameInstant(ZoneId.of("GMT-7"))
            } catch (e: Exception) {
                null
            }

            timePosted.text = when {
                postTime == null -> "Unknown time"
                ChronoUnit.SECONDS.between(postTime, ZonedDateTime.now(ZoneId.of("GMT-7"))) < 60 -> "Just now"
                ChronoUnit.MINUTES.between(postTime, ZonedDateTime.now(ZoneId.of("GMT-7"))) < 60 -> "${ChronoUnit.MINUTES.between(postTime, ZonedDateTime.now(
                    ZoneId.of("GMT-7")))} minutes ago"
                ChronoUnit.HOURS.between(postTime, ZonedDateTime.now(ZoneId.of("GMT-7"))) < 24 -> "${ChronoUnit.HOURS.between(postTime, ZonedDateTime.now(
                    ZoneId.of("GMT-7")))} hours ago"
                ChronoUnit.DAYS.between(postTime, ZonedDateTime.now(ZoneId.of("GMT-7"))) == 1L -> "Yesterday"
                ChronoUnit.DAYS.between(postTime, ZonedDateTime.now(ZoneId.of("GMT-7"))) == 2L -> "Day before yesterday"
                else -> postTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            }

            val totalAnswers = item.totalAnswer ?: 0
            answerCount.text = if (totalAnswers == 1) {
                "$totalAnswers answer"
            } else {
                "$totalAnswers answers"
            }

            root.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                val forumId = item.id.toString()
                val username = item.username
                intent.putExtra("ID", forumId)
                intent.putExtra("USERNAME", username)
                holder.itemView.context.startActivity(intent)
            }

            delete.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        postViewModel.deletePost(item.id.toString())
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                // Apply custom rounded background
                alertDialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)

                // Show the alert dialog
                alertDialog.show()
            }

        }
    }

    override fun getItemCount(): Int = items.size
}

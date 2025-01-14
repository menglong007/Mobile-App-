package kh.edu.rupp.ite.viewmodelv3.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kh.edu.rupp.ite.viewmodelv2.view_model.DetailViewModel
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.databinding.ViewDetailActivityBinding
import kh.edu.rupp.ite.viewmodelv3.model.CommentModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CommentAdapter(
    private val comments: List<CommentModel>,
    private val detailViewModel: DetailViewModel,
    private val userId: String,
    private val context: Context,
    private val binding: ViewDetailActivityBinding,
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById<TextView>(R.id.username)
        val comment = itemView.findViewById<TextView>(R.id.comment)
        val created = itemView.findViewById<TextView>(R.id.created)
        val totalLike = itemView.findViewById<TextView>(R.id.totalLikeComment)
        val totalDislike = itemView.findViewById<TextView>(R.id.totalDisLikeComment)
        val isLike: ImageView = itemView.findViewById(R.id.isLikeComment)
        val isDisLike: ImageView = itemView.findViewById(R.id.isDisLikeComment)
        val isLiked: ImageView = itemView.findViewById(R.id.isLikedComment)
        val isDisLiked: ImageView = itemView.findViewById(R.id.isDisLikedComment)
        val longTabComment = itemView.findViewById<LinearLayout>(R.id.cardComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.username.text = comment.username.ifEmpty { "Anonymous" }
        holder.comment.text = comment.comment.ifEmpty { "No comment provided" }

        holder.totalLike.text = comment.totalLike?.toString() ?: "0"
        holder.totalDislike.text = comment.totalDislike?.toString() ?: "0"

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val postTime = try {
            ZonedDateTime.parse(comment.created, formatter).withZoneSameInstant(ZoneId.of("GMT-7"))
        } catch (e: Exception) {
            null
        }

        holder.created.text = when {
            postTime == null -> "Unknown time"
            ChronoUnit.SECONDS.between(
                postTime,
                ZonedDateTime.now(ZoneId.of("GMT-7"))
            ) < 60 -> "Just now"

            ChronoUnit.MINUTES.between(
                postTime,
                ZonedDateTime.now(ZoneId.of("GMT-7"))
            ) < 60 -> "${
                ChronoUnit.MINUTES.between(
                    postTime,
                    ZonedDateTime.now(ZoneId.of("GMT-7"))
                )
            } minutes ago"

            ChronoUnit.HOURS.between(
                postTime,
                ZonedDateTime.now(ZoneId.of("GMT-7"))
            ) < 24 -> "${
                ChronoUnit.HOURS.between(
                    postTime,
                    ZonedDateTime.now(ZoneId.of("GMT-7"))
                )
            } hours ago"

            ChronoUnit.DAYS.between(
                postTime,
                ZonedDateTime.now(ZoneId.of("GMT-7"))
            ) == 1L -> "Yesterday"

            ChronoUnit.DAYS.between(
                postTime,
                ZonedDateTime.now(ZoneId.of("GMT-7"))
            ) == 2L -> "Day before yesterday"

            else -> postTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
        }


        if (comment.like) {
            holder.isLiked.visibility = View.VISIBLE
            holder.isLike.visibility = View.GONE
        } else {
            holder.isLiked.visibility = View.GONE
            holder.isLike.visibility = View.VISIBLE
        }

        if (comment.disLike) {
            holder.isDisLiked.visibility = View.VISIBLE
            holder.isDisLike.visibility = View.GONE
        } else {
            holder.isDisLiked.visibility = View.GONE
            holder.isDisLike.visibility = View.VISIBLE
        }

        holder.isLike.setOnClickListener {
            detailViewModel.onLikeComment(comment.id.toString())
            notifyItemChanged(position)
        }

        holder.isDisLike.setOnClickListener {
            detailViewModel.onDislikeComment(comment.id.toString())
            notifyItemChanged(position)
        }

        holder.isLiked.setOnClickListener {
            detailViewModel.onLikeComment(comment.id.toString())
            notifyItemChanged(position)
        }

        holder.isDisLiked.setOnClickListener {
            detailViewModel.onDislikeComment(comment.id.toString())
            notifyItemChanged(position)
        }

        binding.sendCommentForUpdate.setOnClickListener {
            val updateText = binding.inputComment.text
            detailViewModel.onUpdateComment(comment.id.toString(), updateText.toString())
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputComment.windowToken, 0)
            binding.inputComment.text.clear()
        }

        if (userId == comment.userId.toString()) {
            holder.longTabComment.setOnLongClickListener {
                val bottomSheetDialog = BottomSheetDialog(it.context)
                val bottomSheetView = LayoutInflater.from(it.context).inflate(
                    R.layout.bottom_sheet,
                    null
                )
                bottomSheetDialog.setContentView(bottomSheetView)

                bottomSheetView.findViewById<TextView>(R.id.edit_comment).setOnClickListener {
                    binding.sendComment.visibility = View.GONE
                    binding.sendCommentForUpdate.visibility = View.VISIBLE
                    val inputComment = binding.inputComment
                    inputComment?.setText(comment.comment)

                    inputComment?.requestFocus()

                    Handler(Looper.getMainLooper()).postDelayed({
                        val imm =
                            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT)
                    }, 200)
                    bottomSheetDialog.dismiss()
                }


                bottomSheetView.findViewById<TextView>(R.id.delete_comment).setOnClickListener {
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete Comment")
                        .setMessage("Are you sure you want to delete this comment?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            detailViewModel.deleteComment(comment.id.toString())
                            bottomSheetDialog.dismiss()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    alertDialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
                    alertDialog.show()
                }



                bottomSheetDialog.show()
                true
            }
        }
    }


    override fun getItemCount(): Int {
        return comments.size
    }
}

package kh.edu.rupp.ite.viewmodelv3.adapter

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.viewmodelv3.activity.DetailActivity
import kh.edu.rupp.ite.viewmodelv3.databinding.ItemHomeBinding
import kh.edu.rupp.ite.viewmodelv3.model.HomeModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HomeAdapter(private val items: List<HomeModel>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            postTitle.text = item.title
            authorName.text = item.username

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
            answerCount.text = if (totalAnswers <= 1) {
                "$totalAnswers answer"
            } else {
                "$totalAnswers answers"
            }

            root.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                val forumId = item.id.toString()
                val username = item.username
                val userId = item.userId.toString()
                intent.putExtra("ID", forumId)
                intent.putExtra("USERNAME", username)
                intent.putExtra("USERID", userId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

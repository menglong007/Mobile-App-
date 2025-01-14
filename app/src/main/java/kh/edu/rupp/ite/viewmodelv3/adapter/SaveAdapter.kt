package kh.edu.rupp.ite.viewmodelv3.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.viewmodelv2.view_model.SavedViewModel
import kh.edu.rupp.ite.viewmodelv3.activity.DetailActivity
import kh.edu.rupp.ite.viewmodelv3.databinding.ItemSaveBinding
import kh.edu.rupp.ite.viewmodelv3.model.SavedModel

class SaveAdapter(
    private val items: List<SavedModel>,
    private val viewModel : SavedViewModel
) : RecyclerView.Adapter<SaveAdapter.SaveViewHolder>() {

    inner class SaveViewHolder(val binding: ItemSaveBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveViewHolder {
        val binding = ItemSaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaveViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            authorName.text = item.username
            postTitle.text = item.title ?: "Untitled"

            val totalAnswers = item.totalAnswer ?: 0
            answerCount.text = if (totalAnswers == 1) {
                "$totalAnswers answer"
            } else {
                "$totalAnswers answers"
            }

            onSaved.setOnClickListener {
                viewModel.onSaveForum(item.forumId)
            }

            root.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                val forumId = item.forumId
                val username = item.username
                val userId = item.userId
                intent.putExtra("ID", forumId)
                intent.putExtra("USERNAME", username)
                intent.putExtra("USERID", userId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

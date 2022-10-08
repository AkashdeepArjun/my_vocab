package com.example.my_vocab.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.databinding.ItemScoreBinding
import java.text.SimpleDateFormat

class ScoresAdapter():RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>(){

        val formatter=SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")



    private val diff_call_back=object : DiffUtil.ItemCallback<Score>() {
        override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
           return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
            return  oldItem.id==newItem.id
        }
    }

    val differ=AsyncListDiffer<Score>(this,diff_call_back)


    var data:List<Score>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {

    val binding=ItemScoreBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner  class ScoreViewHolder(val binding:ItemScoreBinding):ViewHolder(binding.root){

        fun bind(position:Int){
            val item=data.get(position)
            binding.apply {
                tvQuizType.text=item.quiz_type
                tvScore.text=item.score.toString()
                tvDate.text= item.date?.let {
                DateUtils.getRelativeTimeSpanString(it.time)
                }
            }
        }

}


}
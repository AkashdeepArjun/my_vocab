package com.example.my_vocab.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.my_vocab.data.datamodel.Vocab
import com.example.my_vocab.databinding.ItemMyDictionaryBinding
import com.example.my_vocab.viewmodels.MyDictionaryViewModel


class MyDictionaryAdapter(val vm:MyDictionaryViewModel):RecyclerView.Adapter<MyDictionaryAdapter.MyDictionaryViewHolder>()
,Filterable{


    var filtered_list:MutableList<Vocab> = mutableListOf<Vocab>()

    inner class MyDictionaryViewHolder(val binding:ItemMyDictionaryBinding):ViewHolder(binding.root){

        fun bind(word:String,meaning:String){

            binding!!.tvWord.text=word
            binding!!.tvMeaning.text=meaning
        }

    }


    private val diffcallback=object : DiffUtil.ItemCallback<Vocab>() {

        override fun areItemsTheSame(oldItem: Vocab, newItem: Vocab): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Vocab, newItem: Vocab): Boolean {

            return oldItem.hashCode()==newItem.hashCode()
        }
    }


    //   differ that handles the data dublicacy and recycle logic makes recycler view efficient

    val differ=AsyncListDiffer(this,diffcallback)


        // data supplied via differ object
    private var data: MutableList<Vocab>
        get() = differ.currentList
    set(value) = differ.submitList(value)





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDictionaryViewHolder {
        val binding=ItemMyDictionaryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyDictionaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyDictionaryViewHolder, position: Int) {
        val item=data.get(position)
        holder.bind(item.word,item.meaning)

    }

    override fun getItemCount(): Int {

        return data.size
    }


    override fun getFilter(): Filter {
        return object :Filter(){

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val char_string=constraint?.toString()
                if(char_string!!.isEmpty())
                {
                    filtered_list=vm.fetched_dictionary
                }else{
                    var filtered_ones= mutableListOf<Vocab>()

                    filtered_ones.addAll(vm.fetched_dictionary.filter { it.word.contains(char_string,true) })


                    filtered_list=filtered_ones
                }

                return  FilterResults().apply { values=filtered_list }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filtered_list=if(results?.values==null) mutableListOf<Vocab>() else results.values as MutableList<Vocab>
                differ.submitList(filtered_list)

            }
        }
    }
}
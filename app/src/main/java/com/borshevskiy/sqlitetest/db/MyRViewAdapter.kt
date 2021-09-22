package com.borshevskiy.sqlitetest.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.borshevskiy.sqlitetest.EditActivity
import com.borshevskiy.sqlitetest.databinding.RcItemBinding

class MyRViewAdapter(listMain:ArrayList<Note>, context: Context): RecyclerView.Adapter<MyRViewAdapter.MyHolder>() {

    var listArray = listMain
    var _context = context

    class MyHolder(rcItemBinding: RcItemBinding, contextV: Context) : RecyclerView.ViewHolder(rcItemBinding.root) {

        val textTitle = rcItemBinding.textTitle
        val textTime = rcItemBinding.textTime
        val context = contextV
        val rcview = rcItemBinding.rcLayout

        fun setData(note:Note) {
            textTitle.text = note._title
            textTime.text = note._time
            rcview.setOnClickListener {
                val intent = Intent(context,EditActivity::class.java).apply {
                    putExtra(IntentConstants.I_TITLE_KEY, note._title)
                    putExtra(IntentConstants.I_DESC_KEY, note._desc)
                    putExtra(IntentConstants.I_URI_KEY, note._uri)
                    putExtra(IntentConstants.I_ID_KEY, note._id)
                    putExtra(IntentConstants.I_TIME_KEY, note._time)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val rcItemBinding = RcItemBinding.inflate(layoutInflater,parent,false)
        return MyHolder(rcItemBinding,_context)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    fun updateAdapter(listItem:List<Note>) {
        listArray.clear()
        listArray.addAll(listItem)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, dbManager: MyDbManager) {
        dbManager.removeFromDb(listArray[position]._id)
        listArray.removeAt(position)
        notifyItemRangeChanged(0,listArray.size)
        notifyItemRemoved(position)
    }
}
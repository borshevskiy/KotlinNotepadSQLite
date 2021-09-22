package com.borshevskiy.sqlitetest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.borshevskiy.sqlitetest.databinding.ActivityMainBinding
import com.borshevskiy.sqlitetest.db.MyDbManager
import com.borshevskiy.sqlitetest.db.MyRViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val dbManager = MyDbManager(this)
    val rcAdapter = MyRViewAdapter(ArrayList(), this)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    fun onClickNew(view: android.view.View) {
        val intent = Intent(this,EditActivity::class.java)
        startActivity(intent)
    }

    private fun init() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = rcAdapter
        getSwipeMg().attachToRecyclerView(binding.recyclerView)
    }

    private fun fillAdapter(text: String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = dbManager.readDbData(text)
            rcAdapter.updateAdapter(list)
            if (list.size > 0) {
                binding.textViewNoElements.visibility = View.GONE
            } else {
                binding.textViewNoElements.visibility = View.VISIBLE
            }
        }
    }

    private fun getSwipeMg(): ItemTouchHelper {
        return ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                rcAdapter.removeItem(viewHolder.adapterPosition,dbManager)
            }
        })
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                fillAdapter(text!!)
                return true
            }
        })
    }
}
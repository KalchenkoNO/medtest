package com.ce2af4a3.tests.presentation.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ce2af4a3.tests.databinding.ItemTestBinding
import com.ce2af4a3.tests.domain.Test
import javax.inject.Inject

class TestViewHolder(
    private val viewBinding: ItemTestBinding
) : RecyclerView.ViewHolder(viewBinding.root) {
    fun bind(item: Test, onClicked: (Test) -> Unit) {
        viewBinding.title.text = item.name
        viewBinding.root.setOnClickListener { onClicked(item) }
    }
}

class TestsAdapter @Inject constructor() : ListAdapter<Test, TestViewHolder>(DIFF_CALLBACK) {
    private var listener: ((Test) -> Unit)? = null

    private fun onClicked(test: Test) {
        listener?.invoke(test)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun setOnClickListener(listener: (Test) -> Unit) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(getItem(position), ::onClicked)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Test>() {
            override fun areItemsTheSame(oldItem: Test, newItem: Test): Boolean {
                return oldItem.urn == newItem.urn
            }

            override fun areContentsTheSame(oldItem: Test, newItem: Test): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}
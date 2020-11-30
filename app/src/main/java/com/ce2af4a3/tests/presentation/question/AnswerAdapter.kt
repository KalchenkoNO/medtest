package com.ce2af4a3.tests.presentation.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.databinding.ItemAnswerBinding
import com.ce2af4a3.tests.domain.Answer
import javax.inject.Inject

class AnswerViewHolder(
    private val viewBinding: ItemAnswerBinding
) : RecyclerView.ViewHolder(viewBinding.root) {
    fun bind(
        answer: Answer,
        listener: (Answer) -> Unit,
        choseMade: Boolean,
        chosen: Boolean,
        multiPickMode: Boolean
    ) = with(viewBinding) {
        title.text = answer.text
        bindCardView(answer.isCorrect, choseMade, chosen) { listener.invoke(answer) }
        bindIcon(answer.isCorrect, choseMade, chosen, multiPickMode)
    }

    private inline fun bindCardView(
        isCorrect: Boolean,
        choseMade: Boolean,
        chosen: Boolean,
        crossinline listener: () -> Unit,
    ) = with(viewBinding.root) {
        if (!choseMade) setOnClickListener { listener() } else setOnClickListener(null)
        strokeColor = when {
            !choseMade -> ResourcesCompat.getColor(resources, R.color.stroke, null)
            isCorrect && chosen -> ResourcesCompat.getColor(resources, R.color.good, null)
            isCorrect -> ResourcesCompat.getColor(resources, R.color.warn, null)
            chosen -> ResourcesCompat.getColor(resources, R.color.bad, null)
            else -> ResourcesCompat.getColor(resources, R.color.stroke, null)
        }
    }

    private fun bindIcon(
        isCorrect: Boolean,
        choseMade: Boolean,
        chosen: Boolean,
        multiPickMode: Boolean
    ) = with(viewBinding.icon) {
        val isVisible = multiPickMode && !choseMade || choseMade && (isCorrect || chosen)
        visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        setImageResource(
            when {
                choseMade && isCorrect && chosen -> R.drawable.ic_check_circle_black_18dp
                choseMade && isCorrect -> R.drawable.ic_check_circle_18dp
                choseMade && chosen -> R.drawable.ic_cancel_24px
                multiPickMode && chosen -> R.drawable.ic_check_box_24px
                else -> R.drawable.ic_check_box_outline_blank_24px
            }
        )
    }
}

class AnswerAdapter @Inject constructor() : ListAdapter<Answer, AnswerViewHolder>(DIFF_CALLBACK) {
    private var listener: ((List<Answer>) -> Unit)? = null
    private var selectionFinished: Boolean = false
    private var multiselectMode = false
    private val selection = mutableListOf<Answer>()

    fun setSelectionFinishedListener(listener: (List<Answer>) -> Unit) {
        this.listener = listener
    }

    fun checkAnswers() {
        selectionFinished = true
        notifyDataSetChanged()
        listener?.invoke(selection)
    }

    override fun submitList(list: List<Answer>?) {
        multiselectMode = (list?.count { it.isCorrect } ?: 0) > MULTI_PICK_THRESHOLD
        super.submitList(list)
    }

    private fun onItemPicked(answer: Answer) {
        if (selectionFinished) return
        if (selection.contains(answer)) {
            selection.remove(answer)
        } else {
            selection.add(answer)
        }
        if (!multiselectMode) {
            selectionFinished = true
            listener?.invoke(selection)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        return AnswerViewHolder(
            ItemAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val chosen = selection.contains(getItem(position))
        holder.bind(getItem(position), ::onItemPicked, selectionFinished, chosen, multiselectMode)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Answer>() {
            override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
                return oldItem.text == newItem.text
            }
        }
        const val MULTI_PICK_THRESHOLD = 1
    }
}
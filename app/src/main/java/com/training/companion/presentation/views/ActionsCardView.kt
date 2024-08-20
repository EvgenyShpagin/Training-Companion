package com.training.companion.presentation.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.training.companion.R
import com.training.companion.databinding.ActionsCardViewBinding
import com.training.companion.domain.enums.Action
import com.training.companion.presentation.util.iconResource
import com.training.companion.presentation.util.nameResource

class ActionsCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.actionCardViewAppearance,
) : MaterialCardView(context, attributeSet, defStyleAttr) {

    private val binding: ActionsCardViewBinding

    private val actions = Array<Action?>(MAX_ACTIONS_COUNT) { null }
    private val actionButtons: List<MaterialButton>

    private val defaultButtonTintList: ColorStateList

    private var onCloseListener = {}

    private var onActionClickListener = { _: Action -> }

    init {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.actions_card_view,
            this,
            true
        )
        actionButtons = listOf(
            binding.actionButton1,
            binding.actionButton2,
            binding.actionButton3,
            binding.actionButton4
        )

        defaultButtonTintList = binding.actionButton1.backgroundTintList!!
        attributeSet?.let { initAttrs(it) }

        setListeners()
    }

    fun setContentInvisible() {
        binding.actionsCardViewLayout.isInvisible = true
    }

    fun setContentVisible() {
        binding.actionsCardViewLayout.isVisible = true
    }

    fun setActions(actions: List<Action>) {
        assert(actions.size < MAX_ACTIONS_COUNT)
        for (i in 0 until actions.count()) {
            this.actions[i] = actions[i]
            showActionButton(i)
        }
        for (i in actions.count() until MAX_ACTIONS_COUNT) {
            this.actions[i] = null
            actionButtons[i].visibility = GONE
        }
    }

    private fun showActionButton(index: Int) {
        val action = actions[index]!!
        actionButtons[index].apply {
            setText(action.nameResource)
            setIconResource(action.iconResource)
            visibility = VISIBLE
        }
    }

    fun makeEmphasis(action: Action) {
        actions.indexOf(action).also {
            if (it == -1) return
            actionButtons[it].backgroundTintList =
                ColorStateList.valueOf(context.getColor(R.color.dark_orange_40))
        }
    }

    fun clearEmphasis(action: Action) {
        actions.indexOf(action).also {
            if (it == -1) return
            actionButtons[it].backgroundTintList = defaultButtonTintList
        }
    }

    fun removeActions(vararg actions: Action) {
        actions.forEach { action ->
            this.actions.indexOf(action).also {
                if (it == -1) return
                this.actions[it] = null
                actionButtons[it].visibility = GONE
            }
        }
    }

    fun setOnCloseListener(listener: () -> Unit) {
        onCloseListener = listener
    }

    fun setOnActionClickListener(listener: (action: Action) -> Unit) {
        onActionClickListener = listener
    }

    private fun setListeners() {

        fun getOnActionClickListener(index: Int) = OnClickListener {
            onActionClickListener(actions[index]!!)
            onCloseListener()
        }

        binding.actionButton1.setOnClickListener(getOnActionClickListener(index = 0))
        binding.actionButton2.setOnClickListener(getOnActionClickListener(index = 1))
        binding.actionButton3.setOnClickListener(getOnActionClickListener(index = 2))
        binding.actionButton4.setOnClickListener(getOnActionClickListener(index = 3))

        binding.closeButton.setOnClickListener { onCloseListener() }
    }

    private fun removeAllActions() {
        for (i in 0 until MAX_ACTIONS_COUNT) {
            actions[i] = null
            actionButtons[i].visibility = GONE
        }
    }

    private fun showAllButtons() {
        actions.forEachIndexed { index, action ->
            if (action == null) return
            showActionButton(index)
        }
    }

    private fun initAttrs(attributeSet: AttributeSet) {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ActionsCardView,
            0, 0
        ).apply {
            try {
                binding.actionsCardViewTitle.text =
                    getString(R.styleable.ActionsCardView_actions_title)
                        ?: context.getString(R.string.actions_card_view_title)
            } finally {
                recycle()
            }
        }
    }

    companion object {
        private const val MAX_ACTIONS_COUNT = 4
    }
}
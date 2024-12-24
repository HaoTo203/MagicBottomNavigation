package com.omicall.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import vn.hnon.magicbottomnavigation.R

class MagicBottomNavigationItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {
    val icon: ImageView
    val label: TextView
    private var itemId: Int = 0
    private var isSelected: Boolean = false
    private var onAnimationStateChangeListener: OnAnimationStateChangeListener? = null
    private var mainDuration = 300L


    init {
        val view = inflate(context, R.layout.magic_bottom_navigation_item, this)
        icon = view.findViewById(R.id.icon)
        label = view.findViewById(R.id.label)
        clipChildren = false
        clipToPadding = false
    }
    fun isSelectedItem(): Boolean {
        return isSelected
    }

    fun setIsSelectedItem(isSelected: Boolean) {
        if (!isSelected && !this.isSelected) return
        if (isSelected) {
//            label.visibility = View.GONE
            animateOnSelected(- (parent as View).height.toFloat() / 2)
        } else {
//            label.visibility = View.VISIBLE
            icon.isSelected = false
            animateOnSelected(0F)
        }
        this.isSelected = isSelected
    }

    fun itemId(): Int {
        return itemId
    }

    fun setItemId(itemId: Int) {
        this.itemId = itemId
    }

    private fun animateOnSelected(translationY: Float) {
        icon.animate().translationY(translationY).setDuration(mainDuration)
            .withStartAction {
                onAnimationStateChangeListener?.onAnimationStart()
            }
            .withEndAction {
                onAnimationStateChangeListener?.onAnimationEnd(this@MagicBottomNavigationItem)
                icon.isSelected = isSelected
            }
    }

    fun setOnAnimationStateChangeListener(onAnimationStateChangeListener: OnAnimationStateChangeListener) {
        this.onAnimationStateChangeListener = onAnimationStateChangeListener
    }

    fun setMainDuration(mainDuration: Long) {
     this.mainDuration = mainDuration
    }

    interface OnAnimationStateChangeListener {
        fun onAnimationStart()
        fun onAnimationEnd(view: MagicBottomNavigationItem)
    }

}
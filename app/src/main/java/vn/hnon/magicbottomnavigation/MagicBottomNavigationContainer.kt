package com.omicall.app.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout


class MagicBottomNavigationContainer: FrameLayout {

    constructor(context: Context) : super(context) {
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributeFromXml(context, attrs)
        initializeViews()
    }

    private fun initializeViews() {
        apply {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
            clipChildren = false
            clipToPadding = false
        }
    }

    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
    }
}
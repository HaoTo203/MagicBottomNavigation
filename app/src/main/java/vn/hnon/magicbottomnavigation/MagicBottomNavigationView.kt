package com.omicall.app.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.core.view.forEach
import vn.hnon.magicbottomnavigation.R
import vn.hnon.magicbottomnavigation.utils.StyleUtils


class MagicBottomNavigationView : LinearLayout {
    private val defaultHeight = StyleUtils.dip2Pixel(context, 96)
    private val innerDiameter = StyleUtils.dip2Pixel(context, 56)
    private val outerDiameter = StyleUtils.dip2Pixel(context, 72)
    private val mainDuration = 300L
    private lateinit var menu: Menu
    private val backgroundPaint = Paint()
    private lateinit var shader: Shader
    private val shadowPaint = Paint()
    private val outerPaint = Paint()
    private val innerPaint = Paint()
    private lateinit var outerRectF: RectF
    private lateinit var innerRectF: RectF
    private lateinit var outerAnimator: ValueAnimator
    private lateinit var innerAnimator: ValueAnimator
    private var listener: OnItemSelectedListener? = null
    private var selectedItemId: Int? = null
    private var itemTintColor: Int? = null
    private var labelStyle: Int? = null
    private var itemIconSize: Int? = null
    private var isChangingSelection = false

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

    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MagicBottomNavigationView)
        val menuResId = typedArray.getResourceId(R.styleable.MagicBottomNavigationView_menu, 0)
        inflateMenu(menuResId)
        itemTintColor = typedArray.getResourceId(R.styleable.MagicBottomNavigationView_bottomNavIconColor, 0)
        labelStyle = typedArray.getResourceId(R.styleable.MagicBottomNavigationView_bottomNavLabelStyle, 0)
        itemIconSize = typedArray.getDimensionPixelSize(R.styleable.MagicBottomNavigationView_bottomNavIconSize, 0)
        typedArray.recycle()
    }

    private fun inflateMenu(resId: Int) {
        val menu: Menu = PopupMenu(context, null).menu
        MenuInflater(context).inflate(resId, menu)
        this.menu = menu

    }

    private fun initializeViews() {
        backgroundPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))
        backgroundPaint.setColor(resources.getColor(R.color.white, resources.newTheme()))
        backgroundPaint.setShadowLayer(6f, 0f, -3f, resources.getColor(R.color.black20, resources.newTheme()))
        outerPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OUT))
        innerPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))
        innerPaint.setColor(resources.getColor(R.color.info, resources.newTheme()))
        shadowPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))
        outerRectF = RectF(0F, -outerDiameter.toFloat() / 2,
            outerDiameter.toFloat(), outerDiameter.toFloat() / 2)
        innerRectF = RectF(0F, -innerDiameter.toFloat() / 2,
            innerDiameter.toFloat(), innerDiameter.toFloat() / 2)

        // Outer shadow (inside a circle)
        shader = RadialGradient(
            outerRectF.centerX(), outerRectF.centerY(), outerDiameter.toFloat(),
            intArrayOf(Color.TRANSPARENT, resources.getColor(R.color.black, resources.newTheme())), floatArrayOf(0.45F, 0.9F), Shader.TileMode.CLAMP
        )
        shadowPaint.setShader(shader)
        apply {
            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                defaultHeight
            )
            params.setMargins(0, StyleUtils.dip2Pixel(context, 16), 0, 0)
            layoutParams = params
            orientation = HORIZONTAL
            clipChildren = false
            clipToPadding = false
        }


        for (i in 0 until menu.size()) {
            val item = MagicBottomNavigationItem(context)
            item.setItemId(menu.getItem(i).itemId)
            item.setMainDuration(mainDuration)
            item.icon.setImageDrawable(menu.getItem(i).icon)
            itemIconSize?.let {
                val params = LayoutParams(it, it)
                item.icon.layoutParams = params
            }
            itemTintColor?.let {
                item.icon.imageTintList = resources.getColorStateList(it, resources.newTheme())
            }
            item.label.text = menu.getItem(i).title
            labelStyle?.let {
                item.label.setTextAppearance(it)
            }

            val layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F)
            layoutParams.gravity = Gravity.CENTER
            item.layoutParams = layoutParams

            item.setOnClickListener {
                if (isChangingSelection) return@setOnClickListener
                if ((it as MagicBottomNavigationItem).isSelectedItem()) return@setOnClickListener
                isChangingSelection = true
                // Handle center button differently
//                if (item.itemId() == R.id.menu_call) {
//                    listener?.onItemSelected(item.itemId())
//                    return@setOnClickListener
//                }
                runAnimationItemSelected(it)
            }
            item.setOnAnimationStateChangeListener(object :
                MagicBottomNavigationItem.OnAnimationStateChangeListener {
                override fun onAnimationStart() {
                }

                override fun onAnimationEnd(view: MagicBottomNavigationItem) {
                    if (!view.isSelectedItem()) return
                    listener?.onItemSelected(view.itemId())
                    // I add delay here because i cannot detect
                    // when the data from fragment or activity is finish loading
                    Handler(Looper.getMainLooper()).postDelayed({
                        isChangingSelection = false
                    }, 1000)
                }

            })
            this.addView(item)
            // Center icon bigger
////            if (item.itemId() == R.id.menu_call) {
////                item.icon.imageTintList = null
////                val shadowRadius = 6
////                // Add shadow offset because if not the shadow will be clipped
////                val centerIconSize = StyleUtils.dip2Pixel(context, 36) + shadowRadius * 2 //dp
////                val params = LayoutParams(centerIconSize, centerIconSize)
////                item.icon.layoutParams = params
////                val drawable = menu.getItem(i).icon!!
////                // Convert drawable to bitmap
////                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
////                val canvas = Canvas(bitmap)
////                drawable.setBounds(0, 0, canvas.width, canvas.height)
////                drawable.draw(canvas)
////
////                val bitmapWithShadow = addShadowToBitmap(
////                    bitmap,
////                    resources.getColor(R.color.black50, resources.newTheme()),
////                    shadowRadius,
////                    0,
////                    3,
////                    resources.getColor(R.color.info, resources.newTheme())
////                )
////
////                item.icon.setImageBitmap(bitmapWithShadow)
//            }
        }
    }

    private fun addShadowToBitmap(
        originalBitmap: Bitmap,
        shadowColor: Int,
        shadowRadius: Int,
        dx: Int,
        dy: Int,
        tintColor: Int
    ): Bitmap {
        // Create a bitmap bigger so it can contain shadow
        val offset = shadowRadius * 2
        val shadowBitmap = Bitmap.createBitmap(
            originalBitmap.width + offset,
            originalBitmap.height + offset,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(shadowBitmap)

        // Setup Paint
        val shadowPaint = Paint()
        shadowPaint.color = shadowColor
        shadowPaint.setMaskFilter(
            BlurMaskFilter(
                shadowRadius.toFloat(),
                BlurMaskFilter.Blur.NORMAL
            )
        )

        // Draw shadow
        canvas.drawBitmap(
            originalBitmap.extractAlpha(),
            (dx + shadowRadius).toFloat(),
            (dy + shadowRadius).toFloat(),
            shadowPaint
        )

        // Draw main object on top of shadow
        val bitmapPaint = Paint()
        bitmapPaint.setColorFilter(PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(
            originalBitmap,
            shadowRadius.toFloat(),
            shadowRadius.toFloat(),
            bitmapPaint
        )

        return shadowBitmap
    }

    fun setSelectedItemId(itemId: Int) {
        menu.findItem(itemId).let {
            val item = this.getChildAt(it.order) as MagicBottomNavigationItem
            selectedItemId = itemId
            runAnimationItemSelected(item)
        }
    }

    private fun runAnimationItemSelected(it: MagicBottomNavigationItem) {
        outerAnimator = ValueAnimator.ofInt(outerRectF.left.toInt(), it.x.toInt() + it.width / 2 - outerRectF.width().toInt() / 2)
        outerAnimator.setDuration(mainDuration)
        outerAnimator.addUpdateListener { animation ->
            val x = animation.animatedValue as Int
            outerRectF.left = x.toFloat()
            outerRectF.right = x.toFloat() + outerDiameter
            val matrix = Matrix()
            shader.getLocalMatrix(matrix)
            matrix.setTranslate(x.toFloat(), 0F)
            shader.setLocalMatrix(matrix)
            invalidate()
        }
        outerAnimator.start()

        innerAnimator = ValueAnimator.ofInt(innerRectF.left.toInt(), it.x.toInt() + it.width / 2 - innerRectF.width().toInt() / 2)
        innerAnimator.setDuration(mainDuration)
        innerAnimator.addUpdateListener { animation ->
            val x = animation.animatedValue as Int
            innerRectF.left = x.toFloat()
            innerRectF.right = x.toFloat() + innerDiameter
            invalidate()
        }
        innerAnimator.start()

        forEach { view ->
            view as MagicBottomNavigationItem
            view.setIsSelectedItem(false)
        }
        it.setIsSelectedItem(true)

    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (selectedItemId == null) return
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), backgroundPaint)
        canvas.drawRoundRect(outerRectF, outerDiameter.toFloat() / 2, outerDiameter.toFloat() / 2, outerPaint)
        canvas.drawRoundRect(outerRectF, outerDiameter.toFloat() / 2, outerDiameter.toFloat() / 2, shadowPaint)
        canvas.drawRect(outerRectF.left, outerRectF.top, outerRectF.right, 0F, outerPaint)

        canvas.drawRoundRect(innerRectF, innerDiameter.toFloat() / 2, innerDiameter.toFloat() / 2, innerPaint)
    }

    interface OnItemSelectedListener{
        fun onItemSelected(itemId: Int)
    }

}
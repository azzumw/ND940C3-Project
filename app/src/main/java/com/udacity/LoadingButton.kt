package com.udacity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?
    = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var circleColor = 0
    private var loadingRectColor = 0

    private var text = ""

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        color = Color.WHITE
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val loadingRect = Rect()
    private var rectF = RectF(0f, 0f, 80f, 80f)

    private var valueAnimator = ValueAnimator()
    private var progress: Int = 0


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, oldValue, newValue ->
        when (newValue) {
            is ButtonState.Completed -> {
                // Cancel the animation.
                text = context.getString(R.string.button_name)

                progress = 0

                valueAnimator.cancel()
                valueAnimator.setIntValues(0, 360)
                invalidate()
            }

            is ButtonState.Loading -> {

                //positioning the circle
                rectF.offsetTo((width - 270).toFloat(), 30f)

                //animate the rectangle
                //create value animator
                valueAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration = 2000L
                    addUpdateListener {
                        // Update the current progress to use it [onDraw].
                        progress = it.animatedValue as Int

                        if (!isDownloadComplete){
                            repeatCount = ValueAnimator.INFINITE
                            repeatMode = ValueAnimator.RESTART
                        }else{
                            repeatCount = 0
                        }

                        // Redraw the layout to use the new updated value of [progress].
                        invalidate()
                    }

                    // Start the animation.
                    start()
                }

                text = context.getString(R.string.button_loading)
            }

            is ButtonState.Clicked -> {}
        }
    }


    init {
        isClickable = true
        buttonState = ButtonState.Completed

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingRectColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
    }


    override fun performClick(): Boolean {

        if (!radioButtonIsSelected) {
            return super.performClick()
        }

        /*call parent's performClick to ensure
          any click listeners attached are also invoked.
        */
        //Call this view's OnClickListener, if it is defined.
        super.performClick()

        buttonState = if (buttonState == ButtonState.Completed) {
            ButtonState.Loading

        } else {
            ButtonState.Completed
        }

        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //1. Draw Rectangle
        paint.color = loadingRectColor
        loadingRect.set(0, 0, width * progress / 360, height)
        canvas?.drawRect(loadingRect, paint)

        //2. Draw Arc/Circle
        paint.color = circleColor
        canvas?.drawArc(
            rectF,
            0f,
            progress.toFloat(),
            true,
            paint
        )

        //3. Draw Text
        paint.color = Color.WHITE
        canvas?.drawText(
            text,
            width / 2.toFloat(),
            (heightSize + 30) / 2.toFloat(),
            paint
        )

        Log.e("onDrawBefore: progress: ",progress.toString())
        if (isDownloadComplete && progress == 360) {
            Log.e("onDrawAfter: progress: ",progress.toString())
            buttonState = ButtonState.Completed
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}
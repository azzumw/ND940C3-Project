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
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?
    = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

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
                valueAnimator.setIntValues(0,360)
                invalidate()
            }

            is ButtonState.Loading -> {

                //positioning the circle
                rectF.offsetTo((width-270).toFloat(),30f)

                //animate the rectangle
                //create value animator
                Log.e("LoadingButton", "I am in Loading State")
                valueAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration = 2000L
                    addUpdateListener {
//                        // Update the current progress to use it [onDraw].
                        progress = it.animatedValue as Int
                        Log.e("LoadingButton: Progress Value: ", progress.toString())
                        Log.e("LoadingButton: Width Value: ", width.toString())

//                        // Redraw the layout to use the new updated value of [progress].
                        invalidate()
//                        requestLayout()
                    }
//
                    //if download has not started then repeat anim once
                    if (hasDownloadStarted == Download.STARTED) {
                        // Repeat the animation infinitely.
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART
                    }

//                    // Start the animation.
                    start()

                    this.addListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)

                            if (hasDownloadStarted == Download.NOT_STARTED) {
                                buttonState = ButtonState.Completed
                                repeatCount = 0
                            }
                        }
                    })
                }


                //add a listener to the valueAnimator
                //o calculate the circle angle,
                // button background width, etc.
                /*
                * updating these variables according to the current progress,
                * then use these variables to draw the custom view in onDraw
                * */
                text = context.getString(R.string.button_loading)
            }

            is ButtonState.Clicked -> {}
        }
    }


    private var text = ""

    init {
        isClickable = true
        buttonState = ButtonState.Completed

    }

    override fun performClick(): Boolean {

        if(!radioButtonIsSelected){
            return super.performClick()
        }

        /*call parent's performClick to ensure
            any click listeners attached are also invoked.
            */
        //Call this view's OnClickListener, if it is defined.
        super.performClick()

        buttonState = if (buttonState == ButtonState.Completed) {
            Log.e("LoadingButtonState", "COMPLETED")
            ButtonState.Loading

        } else {
            Log.e("LoadingButtonState", "LOADING")
            ButtonState.Completed
        }

        invalidate()
       return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw Rectangle
        paint.color = context.getColor(R.color.colorPrimaryDark)
        loadingRect.set(0, 0, width * progress / 360, height)
        Log.e("Call from onDraw: ", "progress: $progress")
        canvas?.drawRect(loadingRect, paint)

        //Draw Arc/Circle
        paint.color = Color.YELLOW
        canvas?.drawArc(rectF,
            0f,
            progress.toFloat(),
            true,
            paint)

        //3. Draw Text
        paint.color = Color.WHITE
        canvas?.drawText(
            text,
            width / 2.toFloat(),
            (heightSize + 30) / 2.toFloat(),
            paint
        )

        if (isDownloadComplete && progress == 360) {
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
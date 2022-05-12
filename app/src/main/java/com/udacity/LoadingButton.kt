package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlin.properties.Delegates


var radioButtonIsSelected = false

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { property, old, new ->
//        when (new) {
//            ButtonState.Loading -> {
//                // Start the animation.
//            }
//            ButtonState.Completed -> {
//                // Cancel the animation.
//            }
//
//            ButtonState.Clicked->{
//
//            }
//        }
    }


    init {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint().apply {
            color = Color.BLACK
        }

        canvas?.drawText("Button",0f,0f,paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

//    override fun performClick(): Boolean {
//        if(super.performClick())  return true
//
//       if(!radioButtonIsSelected){
//           Toast.makeText(context,"Select an option",Toast.LENGTH_SHORT).show()
//           return true
//       }
//        Toast.makeText(context,"Downloading..",Toast.LENGTH_SHORT).show()
//
//
//        return true
//    }

}
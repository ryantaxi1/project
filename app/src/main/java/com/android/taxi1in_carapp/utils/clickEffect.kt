package com.android.taxi1in_carapp.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.View.VISIBLE
import androidx.core.animation.addListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun onTouchListener(view: View, callBack: () -> Unit) {
    view.setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                animZoomOutClick(v!!)
            } else if (event?.action == MotionEvent.ACTION_UP) {
                animZoomInClick(v!!, 0, v.context)
                CoroutineScope(Dispatchers.Main).launch{
                    callBack()
                }
            } else if (event?.action == MotionEvent.ACTION_CANCEL) {
                animZoomInClick(v!!, 0, v.context)
            }
            return true
        }
    })
}

fun animZoomOutClick(view: View) {
    ObjectAnimator.ofFloat(view, "scaleX", 0.9f).apply {
        interpolator = QuintOut()
        start()
        duration = 200
    }

    ObjectAnimator.ofFloat(view, "scaleY", 0.9f).apply {
        interpolator = QuintOut()
        duration = 200
        start()
    }
}

fun animZoomOut(view: View) {
    ObjectAnimator.ofFloat(view, "scaleX", 0f).apply {
        interpolator = QuintOut()
        start()
        duration = 400
    }

    ObjectAnimator.ofFloat(view, "scaleY", 0f).apply {
        interpolator = QuintOut()
        duration = 400
        start()
    }
}

fun animZoomInClick(view: View, delay: Long, context: Context) {


    var scaleTo = 1f
    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, scaleTo).apply {}
    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, scaleTo).apply {}

    /*scaleX.addUpdateListener {
        //Log.d("Anim",it.getAnimatedValue().toString());
        view.alpha = scaleTo - (it.animatedValue as Float)
    }*/
    val scaleAnim = AnimatorSet()
    scaleAnim.setDuration(250)
    scaleAnim.startDelay = delay
    scaleAnim.play(scaleX).with(scaleY)
    scaleAnim.interpolator = QuintOut()
    CoroutineScope(Dispatchers.Main).launch {
        view.visibility = VISIBLE
    }

    scaleAnim.start()
    scaleAnim.addListener { }
}

class QuintOut : TimeInterpolator {
    override fun getInterpolation(t: Float): Float {
        var t = t
        return 1.let { t -= it; t } * t * t * t * t + 1
    }
}
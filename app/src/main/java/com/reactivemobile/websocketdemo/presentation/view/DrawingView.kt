package com.reactivemobile.websocketdemo.presentation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint: Paint = Paint()
    private val path: Path = Path()

    init {
        paint.isAntiAlias = true;
        paint.strokeWidth = 5f;
        paint.color = Color.BLACK;
        paint.style = Paint.Style.STROKE;
        paint.strokeJoin = Paint.Join.ROUND;
    }

    fun move(x: Float, y: Float) {
        path.moveTo(x, y)
        invalidate()
    }

    fun draw(x: Float, y: Float) {
        path.lineTo(x, y)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPath(path, paint);
        super.onDraw(canvas)
    }

    fun clear() {
        path.reset()
        invalidate()
    }
}
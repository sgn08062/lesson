package com.example.testmlkitocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.BlurMaskFilter
import android.util.AttributeSet
import android.view.View

class OCRImageTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var image: Bitmap? = null
    var ocrResults: List<OCRResult> = emptyList()
    var overlayTextSize: Float = 40f

    private val textPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        textSize = overlayTextSize
    }

    // 배경에 블러 효과를 줄 Paint
    private val bgPaint = Paint().apply {
        color = Color.WHITE
        alpha = 200  // 약간 투명하게 처리
        // BlurMaskFilter를 사용하여 블러 효과 적용 (8f는 블러 반경, NORMAL 모드)
        maskFilter = BlurMaskFilter(4f, BlurMaskFilter.Blur.NORMAL)
    }

    // BlurMaskFilter가 적용된 효과를 제대로 보기 위해 소프트웨어 렌더링 사용
    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * 배경 이미지, OCR 결과, (선택) 텍스트 사이즈를 설정합니다.
     */
    fun setData(image: Bitmap, ocrResults: List<OCRResult>, textSize: Float? = null) {
        this.image = image
        this.ocrResults = ocrResults
        textSize?.let {
            overlayTextSize = it
            textPaint.textSize = it
        }
        invalidate()  // 뷰 갱신
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 배경 이미지 그리기
        image?.let { bmp ->
            canvas.drawBitmap(bmp, 0f, 0f, null)
        }

        // 배경 사각형의 padding (dp -> px 변환)
        val padding = 8 * resources.displayMetrics.density

        // 각 OCR 결과에 대해, 텍스트 영역의 배경에 블러 처리된 사각형과 텍스트를 그림
        for (result in ocrResults) {
            result.boundingBox.let { box ->
                // 배경 사각형 그리기 (boundingBox에 padding 추가)
                canvas.drawRect(
                    box.left.toFloat() - padding,
                    box.top.toFloat() - padding,
                    box.right.toFloat() + padding,
                    box.bottom.toFloat() + padding,
                    bgPaint
                )
                // 텍스트 그리기 (기본적으로 텍스트의 baseline을 고려하여 약간 아래쪽에 위치시킴)
                canvas.drawText(result.text, box.left.toFloat(), box.top.toFloat() + overlayTextSize, textPaint)
            }
        }
    }
}

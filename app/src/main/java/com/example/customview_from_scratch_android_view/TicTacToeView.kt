package com.example.customview_from_scratch_android_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.lang.Float.min
import java.lang.Integer.max
import kotlin.properties.Delegates

class TicTacToeView(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttrs: Int,
    defStyleRes: Int
) : View(context, attributeSet, defStyleAttrs, defStyleRes) {
    private var gridColorValue by Delegates.notNull<Int>()
    private var backgroundColorValue by Delegates.notNull<Int>()
    private var secondPlayerColorValue by Delegates.notNull<Int>()
    private var firstPlayerColorValue by Delegates.notNull<Int>()

    var gameField: TicTacToeField? = null
        set(value) {
            val oldValue = field
            field = value

            oldValue?.listeners?.remove(listener)
            value?.listeners?.add(listener)

            updateViewBoundaries()
            invalidate()
            requestLayout()
        }

    private val fieldArea = RectF(0f, 0f, 0f, 0f)
    private var cellSize: Float = 0f
    private var cellPadding: Int = 0
    private var cellContent = RectF()

    private lateinit var firstPlayerPaint: Paint
    private lateinit var secondPlayerPaint: Paint
    private lateinit var gridPaint: Paint

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttrs: Int) : this(
        context,
        attributeSet,
        defStyleAttrs,
        R.style.TicTacToeDefaultStyle
    )

    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        R.attr.ticTacToeDefaultStyleAttr
    )

    constructor(context: Context) : this(
        context,
        null,
    )

    init {
        if (attributeSet != null) {
            initializeAttributes(attributeSet, defStyleAttrs, defStyleRes)
        } else {
            initializeAttributesByDefault()
        }
        initPaints()

        if (isInEditMode) {
            gameField = TicTacToeField(10, 10)
            gameField?.setCell(1, 7, Cell.PLAYER_1)
            gameField?.setCell(1, 0, Cell.PLAYER_2)
        }
    }

    private fun initPaints() {
        initFirstPlayerPaint()
        initSecondPlayerPaint()
        initGridPaint()
    }

    private fun initFirstPlayerPaint() {
        firstPlayerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        firstPlayerPaint.color = firstPlayerColorValue
        firstPlayerPaint.strokeWidth = 4f
        firstPlayerPaint.style = Paint.Style.STROKE
    }

    private fun initSecondPlayerPaint() {
        secondPlayerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        secondPlayerPaint.color = secondPlayerColorValue
        secondPlayerPaint.strokeWidth = 4f
        secondPlayerPaint.style = Paint.Style.STROKE
    }

    private fun initGridPaint() {
        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColorValue
        gridPaint.strokeWidth = 2f
        gridPaint.style = Paint.Style.STROKE
    }

    private fun initializeAttributes(
        attributeSet: AttributeSet?,
        defStyleAttrs: Int,
        defStyleRes: Int
    ) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.TicTacToeView,
            defStyleAttrs,
            defStyleRes
        )

        with(typedArray) {
            firstPlayerColorValue = getColor(
                R.styleable.TicTacToeView_firstPlayerColor,
                FIRST_PLAYER_COLOR_BY_DEFAULT
            )
            secondPlayerColorValue = getColor(
                R.styleable.TicTacToeView_secondPlayerColor,
                SECOND_PLAYER_COLOR_BY_DEFAULT
            )
            backgroundColorValue = getColor(
                R.styleable.TicTacToeView_ticTacToeBackgroundColor,
                BACKGROUND_COLOR_BY_DEFAULT
            )
            gridColorValue = getColor(
                R.styleable.TicTacToeView_ticTacToeGridColor,
                GRID_COLOR_BY_DEFAULT
            )

        }
        typedArray.recycle()
    }

    private fun initializeAttributesByDefault() {
        firstPlayerColorValue = FIRST_PLAYER_COLOR_BY_DEFAULT
        secondPlayerColorValue = SECOND_PLAYER_COLOR_BY_DEFAULT
        backgroundColorValue = BACKGROUND_COLOR_BY_DEFAULT
        gridColorValue = GRID_COLOR_BY_DEFAULT
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        gameField?.listeners?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        gameField?.listeners?.remove(listener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if ((gameField == null)
            || (cellSize <= 0)
            || (fieldArea.width() <= 0 || fieldArea.height() <= 0)
        ) {
            return
        }

        drawGrid(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        gameField?.let { field ->
            drawHorizontalGrid(field, canvas)
            drawVerticalGrid(field, canvas)
        }
    }

    private fun drawHorizontalGrid(gameField: TicTacToeField, canvas: Canvas) {
        val startX = fieldArea.left
        val endX = fieldArea.right
        val startY = fieldArea.top

        for (lineNumber in 0..gameField.rows) {
            val currentY = startY + lineNumber * cellSize
            canvas.drawLine(startX, currentY, endX, currentY, gridPaint)
        }
    }

    private fun drawVerticalGrid(gameField: TicTacToeField, canvas: Canvas) {
        val startY = fieldArea.top
        val endY = fieldArea.bottom
        val startX = fieldArea.left

        for (lineNumber in 0..gameField.columns) {
            val currentX = startX + lineNumber * cellSize
            canvas.drawLine(currentX, startY, currentX, endY, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        val field = gameField ?: return

        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                drawCell(canvas, row, column, field.getCell(row, column))
            }
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, column: Int, cellType: Cell) {
        if (cellType == Cell.PLAYER_1) {
            drawFirstPlayer(canvas, getRect(row, column))
        } else if (cellType == Cell.PLAYER_2) {
            drawSecondPlayer(canvas, getRect(row, column))
        }
    }

    private fun getRect(row: Int, column: Int): RectF {
        cellContent.left = fieldArea.left + cellPadding + column * cellSize
        cellContent.right = cellContent.left - 2 * cellPadding + cellSize
        cellContent.top = fieldArea.top + row * cellSize + cellPadding
        cellContent.bottom = cellContent.top - 2 * cellPadding + cellSize
        return cellContent
    }

    private fun drawFirstPlayer(canvas: Canvas, drawArea: RectF) {
        canvas.drawLine(
            drawArea.left,
            drawArea.top,
            drawArea.right,
            drawArea.bottom,
            firstPlayerPaint
        )
        canvas.drawLine(
            drawArea.left,
            drawArea.bottom,
            drawArea.right,
            drawArea.top,
            firstPlayerPaint
        )
    }

    private fun drawSecondPlayer(canvas: Canvas, drawArea: RectF) {
        canvas.drawCircle(
            cellContent.centerX(),
            cellContent.centerY(),
            cellContent.width() / 2,
            secondPlayerPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixel = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DESIRED_CELL_SIZE_IN_DP,
            resources.displayMetrics
        ).toInt()

        val desiredColumnsNumber = gameField?.columns ?: 0
        val desiredRowsNumber = gameField?.rows ?: 0

        val approximatedWidth =
            desiredColumnsNumber * desiredCellSizeInPixel + paddingLeft + paddingRight
        val approximatedHeight =
            desiredRowsNumber * desiredCellSizeInPixel + paddingTop + paddingBottom

        val desiredWidth = max(approximatedWidth, minWidth)
        val desiredHeight = max(approximatedHeight, minHeight)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewBoundaries()
    }

    private fun updateViewBoundaries() {
        val field = gameField ?: return

        val safeWidthArea = width - paddingLeft - paddingRight
        val safeHeightArea = height - paddingTop - paddingBottom

        val cellWidth = (safeWidthArea.toFloat() / field.columns)
        val cellHeight = (safeHeightArea.toFloat() / field.rows)

        cellSize = min(cellWidth, cellHeight)
        cellPadding = (cellSize * CELL_PADDING_PERCENTAGE).toInt()

        val areaSupposedToBeWhenCalculatingByWidth = cellSize * field.columns
        val areaSupposedToBeWhenCalculatingByHeight = cellSize * field.rows

        fieldArea.left = paddingLeft + (safeWidthArea - areaSupposedToBeWhenCalculatingByWidth) / 2
        fieldArea.top = paddingTop + (safeHeightArea - areaSupposedToBeWhenCalculatingByHeight) / 2
        fieldArea.right = fieldArea.left + areaSupposedToBeWhenCalculatingByWidth
        fieldArea.bottom = fieldArea.top + areaSupposedToBeWhenCalculatingByHeight
    }

    private val listener: OnTicTacToeFieldBeenChangedListener = {

    }

    companion object {
        private const val FIRST_PLAYER_COLOR_BY_DEFAULT = Color.CYAN
        private const val SECOND_PLAYER_COLOR_BY_DEFAULT = Color.BLUE
        private const val BACKGROUND_COLOR_BY_DEFAULT = Color.WHITE
        private const val GRID_COLOR_BY_DEFAULT = Color.GRAY

        private const val DESIRED_CELL_SIZE_IN_DP = 30f
        private const val CELL_PADDING_PERCENTAGE = 0.2
    }
}
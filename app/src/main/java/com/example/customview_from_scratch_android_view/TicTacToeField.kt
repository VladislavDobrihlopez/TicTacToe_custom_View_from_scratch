package com.example.customview_from_scratch_android_view

enum class Cell {
    PLAYER_1, PLAYER_2, EMPTY
}

typealias OnTicTacToeFieldBeenChangedListener = (Cell) -> Unit

class TicTacToeField(
    val rows: Int,
    val columns: Int
) {
    private val matrix = Array(rows) {
        Array(columns) {
            Cell.EMPTY
        }
    }

    val listeners = mutableSetOf<OnTicTacToeFieldBeenChangedListener>()

    private fun isAddressValid(row: Int, column: Int): Boolean {
        return (row < rows && column < columns) && (row >= 0 && column >= 0)
    }

    fun getCell(row: Int, column: Int): Cell {
        return if (isAddressValid(row, column)) {
            matrix[row][column]
        } else {
            throw IllegalArgumentException("Incorrect arguments were passed")
        }
    }

    fun setCell(row: Int, column: Int, cell: Cell) {
        if (!isAddressValid(row, column)) return
        if (matrix[row][column] == cell) return

        matrix[row][column] = cell
        listeners.forEach {
            it?.invoke(cell)
        }
    }
}
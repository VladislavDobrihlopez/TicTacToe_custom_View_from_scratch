package com.example.customview_from_scratch_android_view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.customview_from_scratch_android_view.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var isTimeForFirstPlayerToMove = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ticTacToeField.onCellTouchedListener = { row, column, gameField ->
            if (gameField.getCell(row, column) == Cell.EMPTY) {
                if (isTimeForFirstPlayerToMove) {
                    gameField.setCell(row, column, Cell.PLAYER_1)
                } else {
                    gameField.setCell(row, column, Cell.PLAYER_2)
                }
                isTimeForFirstPlayerToMove = !isTimeForFirstPlayerToMove
            }
        }

        binding.buttonNextRandomGameField.setOnClickListener {
            val rows = Random.nextInt(7, 12)
            val columns = Random.nextInt(7, 12)
            binding.ticTacToeField.gameField = TicTacToeField(rows, columns).also {
//                for (row in 0 until rows) {
//                    for (column in 0 until columns) {
//                        it.setCell(
//                            row,
//                            column,
//                            when (Random.nextInt(3)) {
//                                0 -> Cell.EMPTY
//                                1 -> Cell.PLAYER_1
//                                2 -> Cell.PLAYER_2
//                                else -> {
//                                    throw IllegalStateException()
//                                }
//                            }
//                        )
//                    }
//                }
            }
        }
    }
}
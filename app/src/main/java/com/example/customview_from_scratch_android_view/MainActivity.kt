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

        val askForClearingDialog = EnsureClearingFieldDialogFragment()

        askForClearingDialog.callback = {
            if (it) {
                binding.ticTacToeField.clearGameField()
            }
        }

        val askForGeneratingDialog = EnsureGeneratingFieldDialogFragment()

        askForGeneratingDialog.callback = {
            if (it) {
                val rows = Random.nextInt(7, 12)
                val columns = Random.nextInt(7, 12)
                binding.ticTacToeField.gameField = TicTacToeField(rows, columns)
            }
        }

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

        binding.buttonClearField.setOnClickListener {
            askForClearingDialog.show(supportFragmentManager, "dialog")
        }

        binding.buttonNextRandomGameField.setOnClickListener {
            askForGeneratingDialog.show(supportFragmentManager, "dialog")
        }
    }
}
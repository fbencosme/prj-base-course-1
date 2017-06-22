package com.altice.eteco.course.basic.ticTacToe

enum class Type {
    Cross, Nought
}

data class TicTacToe(
    val type : Type
)
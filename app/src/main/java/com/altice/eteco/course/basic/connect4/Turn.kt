package com.altice.eteco.course.basic.connect4

enum class Turn {

    Red, Yellow, Black;

    infix fun checkVertically(board: List<Bucket>) :  Boolean {
        var res = false

        for (column in 0..3) {
            for (row in 0..6) {

                val match = board.filter {
                    it.row == column   && it.column == row ||
                    it.row == column+1 && it.column == row ||
                    it.row == column+2 && it.column == row ||
                    it.row == column+3 && it.column == row
                }

                if (match.count { it.turn == this } == 4)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

    infix fun checkHorizontally(board: List<Bucket>) :  Boolean {
        var res = false

        for (row in 0..3) {
            for (column in 0..6) {

                val match = board.filter {
                    it.row == column && it.column == row   ||
                    it.row == column && it.column == row+1 ||
                    it.row == column && it.column == row+2 ||
                    it.row == column && it.column == row+3
                }

                if (match.count { it.turn == this } == 4)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

    infix fun checkDiagonallyUp(board: List<Bucket>) :  Boolean {
        var res = false

        for (column in 3..6) {
            for (row in 0..3) {

                val match = board.filter {
                    it.row == column   && it.column == row   ||
                    it.row == column-1 && it.column == row+1 ||
                    it.row == column-2 && it.column == row+2 ||
                    it.row == column-3 && it.column == row+3
                }

                if (match.count { it.turn == this } == 4)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }

    infix fun checkDiagonallyDown(board: List<Bucket>) :  Boolean {
        var res = false

        for (column in 3..6) {
            for (row in 0..5) {

                var match = board.filter {
                    it.row == column   && it.column == row   ||
                    it.row == column-1 && it.column == row-1 ||
                    it.row == column-2 && it.column == row-2 ||
                    it.row == column-3 && it.column == row-3
                }

                if (match.count { it.turn == this } == 4)
                {
                    res = true
                    return res
                }
            }
        }

        return res
    }
}
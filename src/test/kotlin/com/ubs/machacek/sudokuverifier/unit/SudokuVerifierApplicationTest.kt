package com.ubs.machacek.sudokuverifier.unit

import com.fasterxml.jackson.databind.ObjectMapper
import com.ubs.machacek.sudokuverifier.model.SudokuException
import com.ubs.machacek.sudokuverifier.service.SudokuService
import com.ubs.machacek.sudokuverifier.util.TracingContextElement
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Error message asserts are commented, since you never know, which coroutine
 * finishes first and matching regex for all variants would be quite expensive
 */
class SudokuVerifierApplicationTest {

    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!

    val service = SudokuService(ObjectMapper(), TracingContextElement())

    @Test
    fun ok() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(3, 1, 6, 5, 7, 8, 4, 9, 2),
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(4, 8, 7, 6, 2, 9, 5, 3, 1),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, 7),
            arrayOf(9, 7, 4, 8, 6, 3, 1, 2, 5),
            arrayOf(8, 5, 1, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
        service.validateMatrix(matrix)
    }

    @Test
    fun nok_duplicateNumber() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(3, 1, 6, 5, 7, 8, 4, 9, 2),
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(4, 8, 7, 6, 2, 9, 5, 3, 1),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, 7),
            arrayOf(9, 7, 4, 8, 6, 9, 1, 2, 5),
            arrayOf(8, 5, 1, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
        expectedEx.expect(SudokuException::class.java)
        //expectedEx.expectMessage("Sum of part [4,1,5,8,6,9,7,9,2] is not 45")
        service.validateMatrix(matrix)
    }

    @Test
    fun nonok_invalidateNumber() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(13, 1, 6, 5, 7, 8, 4, 9, 2),
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(4, 8, 7, 6, 2, 9, 5, 3, 1),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, 7),
            arrayOf(9, 7, 4, 8, 6, 3, 1, 2, 5),
            arrayOf(8, 5, 1, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
        expectedEx.expect(SudokuException::class.java)
        //expectedEx.expectMessage("Invalidate number inside row [13,1,6,5,2,9,4,8,7]")
        service.validateMatrix(matrix)
    }

    @Test
    fun nonok_grid() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(3, 1, 6, 5, 7, 8, 4, 9, 2),
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(4, 8, 1, 6, 2, 9, 5, 3, null),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, 7),
            arrayOf(9, 7, 4, 8, 6, 3, 1, 2, 5),
            arrayOf(8, 5, null, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
        expectedEx.expect(SudokuException::class.java)
        //expectedEx.expectMessage("Sum of part [3,1,6,5,2,9,4,8,1] is not 45")
        service.validateMatrix(matrix)
    }

    @Test
    fun nonok_grid_lastGrid() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(3, 1, 6, 5, 7, 8, 4, 9, 2),
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(4, 8, 7, 6, 2, 9, 5, 3, 1),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, null),
            arrayOf(9, 7, 4, 8, 6, 3, 1, 2, 5),
            arrayOf(8, 5, 1, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(null, 4, 5, 2, 8, 6, 3, 1, 7)
        )
        expectedEx.expect(SudokuException::class.java)
        //expectedEx.expectMessage("Sum of part [2,5,6,8,7,4,3,1,7] is not 45")
        service.validateMatrix(matrix)
    }

    @Test
    fun ok_row_null() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(null, null, null, null, null, null, null, null, null),
            arrayOf(4, 8, 7, 6, 2, 9, 5, 3, 1),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, 7),
            arrayOf(9, 7, 4, 8, 6, 3, 1, 2, 5),
            arrayOf(8, 5, 1, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
        service.validateMatrix(matrix)
    }

    @Test
    fun ok_grid_null() {
        val matrix = arrayOf<Array<Int?>>(
            arrayOf(3, 1, 6, null, null, null, 4, 9, 2),
            arrayOf(5, 2, 9, null, null, null, 7, 6, 8),
            arrayOf(4, 8, 7, null, null, null, 5, 3, 1),
            arrayOf(2, 6, 3, 4, 1, 5, 9, 8, 7),
            arrayOf(9, 7, 4, 8, 6, 3, 1, 2, 5),
            arrayOf(8, 5, 1, 7, 9, 2, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
        service.validateMatrix(matrix)
    }
}

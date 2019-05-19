package com.ubs.machacek.sudokuverifier.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ubs.machacek.sudokuverifier.model.ErrorCode
import com.ubs.machacek.sudokuverifier.model.throwError
import com.ubs.machacek.sudokuverifier.util.TracingContextElement
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.Supplier
import org.springframework.stereotype.Service

@Service
class SudokuService(
    private val objectMapper: ObjectMapper,
    private val tracingProvider: TracingContextElement
) {

    companion object {
        val log = LogManager.getLogger(SudokuService::class.java)

        private val ALLOWED_NUMBERS = IntRange(1, 9)
        const val MATRIX_SIZE = 9
        private const val SUM_OF_AXIS = 45
        private const val GRID_SIZE = 3
    }

    fun validateMatrix(matrix: Array<Array<Int?>>) {
        log.info(Supplier { "operation=validateMatrixBegin, matrix=${objectMapper.writeValueAsString(matrix)}" })
        runBlocking {
            validateMatrixAsync(matrix = matrix).await()
        }.also {
            log.info(Supplier { "operation=validateMatrixEnd" })
        }
    }

    /**
     * Performs all 3 validations types using coroutine to achieve asynchronous-like computation
     * without demanding expensive new threads, since the validation tasks are really not that
     * complex.
     * Each validation is basically just row validation, once performed on original matrix, once
     * on transposed one (to check columns) and once on inner small 3x3 grids.
     * Each coroutines context is also provided with tracing context for debug purposes.
     */
    suspend fun validateMatrixAsync(matrix: Array<Array<Int?>>): Deferred<Job> {
        return GlobalScope.async(context = tracingProvider) {
            launch { validateRowsInMatrix(matrix = transformInnerGridMatrix(matrix = matrix)) }
            launch { validateRowsInMatrix(matrix = transposeMatrix(matrix = matrix)) }
            launch { validateRowsInMatrix(matrix = matrix) }
        }
    }

    private suspend fun validateRowsInMatrix(matrix: Array<Array<Int?>>) {
        log.debug(Supplier { "operation=validateRowsInMatrixBegin" })
        matrix.forEach { row -> validatePart(row = row) }.also {
            log.debug(Supplier { "operation=validateRowsInMatrixEnd" })
        }
    }

    private suspend fun validatePart(row: Array<Int?>) {
        val notNullRow = row.filterNotNull()
        notNullRow.forEach { number ->
            if (!SudokuService.ALLOWED_NUMBERS.contains(number)) {
                throwError(
                    errorCode = ErrorCode.SUDOKU_ERR_002_INVALID_CHARACTERS,
                    errorMessage = "Invalidate number inside row ${objectMapper.writeValueAsString(row)}"
                )
            }
        }
        if (notNullRow.size == MATRIX_SIZE) {
            if (notNullRow.sum() != SUM_OF_AXIS) throwError(
                errorCode = ErrorCode.SUDOKU_ERR_003_INVALID_SUM,
                errorMessage = "Sum of part ${objectMapper.writeValueAsString(row)} is not $SUM_OF_AXIS"
            )
        }
        if (notNullRow.size != notNullRow.toSet().size) {
            throwError(
                errorCode = ErrorCode.SUDOKU_ERR_004_DUPLICATE_CHARACTERS,
                errorMessage = "Duplicate number axis ${objectMapper.writeValueAsString(row)}"
            )
        }
    }

    private fun transposeMatrix(matrix: Array<Array<Int?>>): Array<Array<Int?>> {
        log.debug(Supplier { "operation=transformMatrixBegin, type=transpose" })
        val transpose = Array(MATRIX_SIZE) { arrayOfNulls<Int?>(MATRIX_SIZE) }
        for (column in 0 until MATRIX_SIZE) {
            for (row in 0 until MATRIX_SIZE) {
                transpose[row][column] = matrix[column][row]
            }
        }
        return transpose.also {
            log.info(Supplier { "operation=transformMatrixEnd" })
        }
    }

    private fun transformInnerGridMatrix(matrix: Array<Array<Int?>>): Array<Array<Int?>> {
        log.debug(Supplier { "operation=transformMatrixBegin, type=innerGrids" })
        val gridArray = mutableListOf<Array<Int?>>()
        for (y_grid_position in 0..6 step 3) {
            for (x_grid_position in 0..6 step 3) {
                val grid = arrayOfNulls<Int?>(MATRIX_SIZE)
                for (rowOfGrid in 0 until GRID_SIZE) {
                    matrix[y_grid_position + rowOfGrid].copyInto(
                        destination = grid,
                        destinationOffset = rowOfGrid * GRID_SIZE,
                        startIndex = x_grid_position,
                        endIndex = x_grid_position + GRID_SIZE
                    )
                }
                gridArray.add(grid)
            }
        }
        return gridArray.toTypedArray().also {
            log.debug(Supplier { "operation=transformMatrixEnd" })
        }
    }
}
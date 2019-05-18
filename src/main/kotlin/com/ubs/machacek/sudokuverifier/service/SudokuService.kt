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
import kotlin.coroutines.EmptyCoroutineContext

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
            validateMatrixAsync(matrix).await()
        }.also {
            log.info(Supplier { "operation=validateMatrixEnd" })
        }
    }

    fun validateMatrixAsync(matrix: Array<Array<Int?>>): Deferred<Job> {
        val context = EmptyCoroutineContext
        context.plus(tracingProvider)
        return GlobalScope.async(context = tracingProvider) {
            launch { validateRowsInMatrix(matrix = matrix) }
            launch { validateRowsInMatrix(matrix = transposeMatrix(matrix = matrix)) }
            launch { validateGridsInMatrix(matrix = matrix) }
        }
    }

    private suspend fun validateRowsInMatrix(matrix: Array<Array<Int?>>) {
        log.info(Supplier { "operation=validateRowsInMatrixBegin" })
        matrix.forEach { row -> validatePart(row = row) }.also {
            log.info(Supplier { "operation=validateRowsInMatrixEnd" })
        }
    }

    private fun validatePart(row: Array<Int?>) {
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
                errorMessage = "Sum of part ${objectMapper.writeValueAsString(row)} is not $SUM_OF_AXIS "
            )
        }
        if (notNullRow.size != notNullRow.toSet().size) {
            throwError(
                errorCode = ErrorCode.SUDOKU_ERR_004_DUPLICATE_CHARACTERS,
                errorMessage = "Duplicate number axis $row"
            )
        }
    }

    private fun transposeMatrix(matrix: Array<Array<Int?>>): Array<Array<Int?>> {
        val transpose = Array(MATRIX_SIZE) { arrayOfNulls<Int?>(MATRIX_SIZE) }
        for (column in 0 until MATRIX_SIZE) {
            for (row in 0 until MATRIX_SIZE) {
                transpose[row][column] = matrix[column][row]
            }
        }
        return transpose
    }

    private suspend fun validateGridsInMatrix(matrix: Array<Array<Int?>>) {
        log.info(Supplier { "operation=validateGridsInMatrixBegin" })
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
                validatePart(grid)
            }
        }
        log.info(Supplier { "operation=validateGridsInMatrixEnd" })
    }
}
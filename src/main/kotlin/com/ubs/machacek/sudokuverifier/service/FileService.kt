package com.ubs.machacek.sudokuverifier.service

import com.ubs.machacek.sudokuverifier.model.ErrorCode
import com.ubs.machacek.sudokuverifier.model.SudokuException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.Supplier
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.*

@Service
class FileService {

    companion object {
        val log = LogManager.getLogger(FileService::class.java)
        private const val COMMA_DELIMITER = ','
    }

    fun readCsv(file: InputStream): Array<Array<Int?>> {
        log.info( Supplier { "operation=readCsvBegin" })

        val matrix = Array(SudokuService.MATRIX_SIZE) { arrayOfNulls<Int?>(SudokuService.MATRIX_SIZE) }
        try {
            Scanner(file).use { scanner ->
                for (i in 0 until SudokuService.MATRIX_SIZE) {
                    scanner.nextLine().split(COMMA_DELIMITER).map { it.trim() }.map { it.toIntOrNull() }
                        .toTypedArray().also { row ->
                            if (row.size != 9) {
                                throw SudokuException(
                                    ErrorCode.SUDOKU_ERR_001_INVALID_SIZE,
                                    "Invalid number of columns in input file's matrix"
                                )
                            }
                            matrix[i] = row
                        }
                }
            }
            return matrix.also {
                log.info( Supplier { "operation=readCsvEnd" }) }
        } catch (e: NoSuchElementException) {
            throw SudokuException(
                ErrorCode.SUDOKU_ERR_001_INVALID_SIZE,
                "Invalid number of rows in input file's matrix"
            )
        }
    }
}
package com.ubs.machacek.sudokuverifier.service

import com.ubs.machacek.sudokuverifier.configuration.FileParserConfiguration
import com.ubs.machacek.sudokuverifier.model.ErrorCode
import com.ubs.machacek.sudokuverifier.model.SudokuException
import com.ubs.machacek.sudokuverifier.model.throwError
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.Supplier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.*

@Service
@EnableConfigurationProperties(FileParserConfiguration::class)
class FileService(
    private val props: FileParserConfiguration
) {

    companion object {
        val log = LogManager.getLogger(FileService::class.java)
    }

    init {
        log.info("Loaded configuration=[delimiter='${props.delimiter}']")
    }

    fun readCsv(file: InputStream): Array<Array<Int?>> {
        log.info(Supplier { "operation=readCsvBegin" })
        val matrix = Array(SudokuService.MATRIX_SIZE) { arrayOfNulls<Int?>(SudokuService.MATRIX_SIZE) }
        return try {
            Scanner(file).use { scanner ->
                for (i in 0 until SudokuService.MATRIX_SIZE) {
                    /**
                     * converting stream to sequence allows better performance, since sequences are lazy and just decorates the previous one
                     * with additional operation, but on the other side we cannot type it straight to typed Array since inside the sequence, we
                     * dont have the information about the other elements. So we convert it to List first.
                     * If given element cannot be cast to Integer, we consider it as 'null'. We also get rid of each element after the first 9.
                     */
                    scanner.nextLine().split(props.delimiter).asSequence()
                        .map { it.trim() }
                        .map { it.toIntOrNull() }
                        .take(9)
                        .toList()
                        .toTypedArray().also { row ->
                            log.debug( Supplier { "operation=checkRowSize, rowSize=${row.size}" })
                            if (row.size < 9) {
                                throwError(
                                    errorCode = ErrorCode.SUDOKU_ERR_001_INVALID_SIZE,
                                    errorMessage = "Invalid number of columns in input file's matrix"
                                )
                            }
                            matrix[i] = row
                        }
                }
            }
            matrix.also {
                log.info(Supplier { "operation=readCsvEnd" })
            }
        } catch (e: NoSuchElementException) {
            throw SudokuException(
                ErrorCode.SUDOKU_ERR_001_INVALID_SIZE,
                "Invalid number of rows in input file's matrix"
            )
        }
    }
}
package com.ubs.machacek.sudokuverifier.unit

import com.ubs.machacek.sudokuverifier.configuration.FileParserConfiguration
import com.ubs.machacek.sudokuverifier.model.SudokuException
import com.ubs.machacek.sudokuverifier.service.FileService
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito
import java.io.File
import kotlin.test.assertEquals

class FileServiceTest {

    /**
     * Mock used to avoid initializing Spring context
     */
    private fun props() = run {
        val mock = Mockito.mock(FileParserConfiguration::class.java)
        Mockito.`when`(mock.delimiter).thenReturn(",")
        mock
    }

    private val service = FileService(props())

    @Rule
    @JvmField
    val expectedEx: ExpectedException = ExpectedException.none()

    @Test
    fun parseFile_ok() {
        val fileToTest = File(this.javaClass.getResource("/testFile_ok.txt").file).inputStream()
        val result = service.readCsv(file = fileToTest)
        val expected = getMatrix_ok()
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                assertEquals(expected[i][j], result[i][j])
            }
        }
    }

    @Test
    fun parseFile_ok_blankGrid() {
        val fileToTest = File(this.javaClass.getResource("/testFile_ok_blankGrid.txt").file).inputStream()
        val result = service.readCsv(file = fileToTest)
        val expected = getMatrix_blankGrid()
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                assertEquals(expected[i][j], result[i][j])
            }
        }
    }

    @Test
    fun parseFile_nok_missingRow() {
        expectedEx.expect(SudokuException::class.java)
        expectedEx.expectMessage("Invalid number of rows in input file's matrix")

        val fileToTest = File(this.javaClass.getResource("/testFile_nok_missingRow.txt").file).inputStream()
        service.readCsv(file = fileToTest)
    }

    @Test
    fun parseFile_nok_missingColumn() {
        expectedEx.expect(SudokuException::class.java)
        expectedEx.expectMessage("Invalid number of columns in input file's matrix")

        val fileToTest = File(this.javaClass.getResource("/testFile_nok_missingColumn.txt").file).inputStream()
        service.readCsv(file = fileToTest)
    }

    private fun getMatrix_ok(): Array<Array<Int?>> {
        return arrayOf<Array<Int?>>(
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
    }

    private fun getMatrix_blankGrid(): Array<Array<Int?>> {
        return arrayOf<Array<Int?>>(
            arrayOf(3, 1, 6, 5, 7, 8, 4, 9, 2),
            arrayOf(5, 2, 9, 1, 3, 4, 7, 6, 8),
            arrayOf(4, 8, 7, 6, 2, 9, 5, 3, 1),
            arrayOf(2, 6, 3, null, null, null, 9, 8, 7),
            arrayOf(9, 7, 4, null, null, null, 1, 2, 5),
            arrayOf(8, 5, 1, null, null, null, 6, 4, 3),
            arrayOf(1, 3, 8, 9, 4, 7, 2, 5, 6),
            arrayOf(6, 9, 2, 3, 5, 1, 8, 7, 4),
            arrayOf(7, 4, 5, 2, 8, 6, 3, 1, 9)
        )
    }
}
package com.ubs.machacek.sudokuverifier.ct

import com.ubs.machacek.sudokuverifier.SudokuVerifierApplication
import io.restassured.RestAssured.given
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [SudokuVerifierApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SudokuCT {

    @LocalServerPort
    val randomServerPort: Int = 0

    @Test
    fun sudokuValidation_ok() {
        given().port(randomServerPort)
            .`when`()
            .multiPart("file", File(this.javaClass.getResource("/testFile_ok.txt").file))
            .post("/sudoku")
            .then().statusCode(204).body(Matchers.isEmptyString())
    }

    @Test
    fun sudokuValidation_nok_missingColumn() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart("file", File(this.javaClass.getResource("/testFile_nok_missingColumn.txt").file))
            .post("/sudoku")
            .then().statusCode(422).extract().response()

        assertEquals("SUDOKU_ERR_001_INVALID_SIZE", response.jsonPath().getString("code"))
        assertEquals("Invalid number of columns in input file's matrix", response.jsonPath().getString("message"))
    }

    @Test
    fun sudokuValidation_nok_missingRow() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart("file", File(this.javaClass.getResource("/testFile_nok_missingRow.txt").file))
            .post("/sudoku")
            .then().statusCode(422).extract().response()

        assertEquals("SUDOKU_ERR_001_INVALID_SIZE", response.jsonPath().getString("code"))
        assertEquals("Invalid number of rows in input file's matrix", response.jsonPath().getString("message"))
    }

    @Test
    fun sudokuValidation_nok_duplicateNumbers() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart("file", File(this.javaClass.getResource("/testFile_nok_duplicateNumber.txt").file))
            .post("/sudoku")
            .then().statusCode(422).extract().response()

        assertEquals("SUDOKU_ERR_003_INVALID_SUM", response.jsonPath().getString("code"))
    }

}
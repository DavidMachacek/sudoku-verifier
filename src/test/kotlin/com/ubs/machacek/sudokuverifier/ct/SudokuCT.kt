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
@SpringBootTest(
    classes = [SudokuVerifierApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class SudokuCT {

    @LocalServerPort
    val randomServerPort: Int = 0

    companion object {
        const val PART_NAME = "sudoku"
        const val SUDOKU_PATH = "/sudoku"
    }

    @Test
    fun sudokuValidation_ok() {
        given().port(randomServerPort)
            .`when`()
            .multiPart(PART_NAME, File(this.javaClass.getResource("/testFile_ok.txt").file))
            .post(SUDOKU_PATH)
            .then().statusCode(204).body(Matchers.isEmptyString())
    }

    @Test
    fun sudokuValidation_nok_missingColumn() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart(PART_NAME, File(this.javaClass.getResource("/testFile_nok_missingColumn.txt").file))
            .post(SUDOKU_PATH)
            .then().statusCode(422).extract().response()

        assertEquals("SUDOKU_ERR_001_INVALID_SIZE", response.jsonPath().getString("code"))
        assertEquals("Invalid number of columns in input file's matrix", response.jsonPath().getString("message"))
    }

    @Test
    fun sudokuValidation_nok_missingRow() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart(PART_NAME, File(this.javaClass.getResource("/testFile_nok_missingRow.txt").file))
            .post(SUDOKU_PATH)
            .then().statusCode(422).extract().response()

        assertEquals("SUDOKU_ERR_001_INVALID_SIZE", response.jsonPath().getString("code"))
        assertEquals("Invalid number of rows in input file's matrix", response.jsonPath().getString("message"))
    }

    @Test
    fun sudokuValidation_nok_wrongCharacters() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart(PART_NAME, File(this.javaClass.getResource("/testFile_nok_sumIsNot45.txt").file))
            .post(SUDOKU_PATH)
            .then().statusCode(422).extract().response()

        assertEquals("SUDOKU_ERR_003_INVALID_SUM", response.jsonPath().getString("code"))
    }

    @Test
    fun sudokuValidation_ok_wrongCharacters() {
        given().port(randomServerPort)
            .`when`()
            .multiPart(PART_NAME, File(this.javaClass.getResource("/testFile_ok_wrongCharacters.txt").file))
            .post(SUDOKU_PATH)
            .then().statusCode(204)
    }

    @Test
    fun sudokuValidation_nok_duplicateCharacters() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart(PART_NAME, File(this.javaClass.getResource("/testFile_nok_5x5.csv").file))
            .post(SUDOKU_PATH)
            .then().statusCode(422).extract().response()
        assertEquals("SUDOKU_ERR_004_DUPLICATE_CHARACTERS", response.jsonPath().getString("code"))
        assertEquals("Duplicate number axis [5,5,5,5,5,5,5,5,5]", response.jsonPath().getString("message"))
    }

    @Test
    fun sudokuValidation_nok_wrongPart() {
        val response = given().port(randomServerPort)
            .`when`()
            .multiPart("blablabla", File(this.javaClass.getResource("/testFile_nok_5x5.csv").file))
            .post(SUDOKU_PATH)
            .then().statusCode(422).extract().response()
        assertEquals("SUDOKU_ERR_006_MISSING_FILE", response.jsonPath().getString("code"))
        assertEquals("A Sudoku file is required in request's Multipart part 'sudoku'", response.jsonPath().getString("message"))
    }

    @Test
    fun sudokuValidation_nok_noFile() {
        val response = given().port(randomServerPort)
            .`when`()
            .post(SUDOKU_PATH)
            .then().statusCode(422).extract().response()
        assertEquals("SUDOKU_ERR_006_MISSING_FILE", response.jsonPath().getString("code"))
        assertEquals("Multipart request is required", response.jsonPath().getString("message"))
    }
}
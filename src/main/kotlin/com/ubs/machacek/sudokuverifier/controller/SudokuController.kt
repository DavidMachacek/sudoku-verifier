package com.ubs.machacek.sudokuverifier.controller

import com.ubs.machacek.sudokuverifier.service.FileService
import com.ubs.machacek.sudokuverifier.service.SudokuService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.Supplier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import kotlin.system.measureTimeMillis

@Api("API for Sudoku related operations")
@RequestMapping("/sudoku")
@RestController
class SudokuController(
    private val sudokuService: SudokuService,
    private val fileService: FileService
) {

    companion object {
        val log = LogManager.getLogger(SudokuController::class.java)
    }

    @ApiOperation("Validates file with Sudoku")
    @ApiResponses(
        ApiResponse(code = 204, message = "Sudoku is OK"),
        ApiResponse(code = 422, message = "Invalid is not OK. Reason is supplied in returning error data object", response = ErrorTO::class)
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    fun validateSudokuFile(
        @RequestParam(value = "sudoku") file: MultipartFile
    ) {
        log.info(Supplier { "receive=[POST]/sudoku, callType=REST, operation=validateSudokuBegin, inputParams=[fileName=${file.name}, fileContentType=${file.contentType}]" })
        measureTimeMillis {
            fileService.readCsv(ByteArrayInputStream(file.bytes)).let { matrix ->
                sudokuService.validateMatrix(matrix)
            }
        }.also { duration ->
            log.info(Supplier { "operation=validateSudokuEnd, duration=$duration" })
        }
    }
}
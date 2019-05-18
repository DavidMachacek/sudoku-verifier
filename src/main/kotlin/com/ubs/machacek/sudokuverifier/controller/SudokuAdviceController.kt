package com.ubs.machacek.sudokuverifier.controller

import com.ubs.machacek.sudokuverifier.model.SudokuException
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.util.Supplier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class SudokuAdviceController {

    companion object {
        private val log = LogManager.getLogger(SudokuAdviceController::class.java)
    }

    @ExceptionHandler(SudokuException::class)
    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleSudokuException(
        e: SudokuException,
        request: HttpServletRequest
    ): ErrorTO {
        return ErrorTO(code = e.errorCode!!.name, message = e.message).also {
            log.error(Supplier { "receive=[${request.method}]${request.servletPath} , errorCode=${e.errorCode}, message='${e.message}'" })
        }
    }
}

@ApiModel(description = "Single validation error object")
data class ErrorTO(
    @field: ApiModelProperty(
        "Error code to identify occurred error.",
        required = true,
        example = "SUDOKU_ERR_002_INVALID_CHARACTERS"
    )
    val code: String,
    @field: ApiModelProperty(
        "Message briefly describing error and part of sudoku, which caused it. Message is human readable always in english language.",
        required = false,
        example = "Invalidate number inside row [13,1,6,5,2,9,4,8,7]"
    )
    val message: String? = null
)
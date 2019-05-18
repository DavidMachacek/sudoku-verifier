package com.ubs.machacek.sudokuverifier.model

class SudokuException(
    val errorCode: ErrorCode?,
    override val message: String? = null,
    cause: Throwable? = null
) : RuntimeException(cause)


@Throws(SudokuException::class)
fun throwError(errorCode: ErrorCode, errorMessage: String) {
    throw SudokuException(
        errorCode = errorCode,
        message = errorMessage
    )
}
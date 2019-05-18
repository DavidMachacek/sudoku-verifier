package com.ubs.machacek.sudokuverifier

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SudokuVerifierApplication

fun main(args: Array<String>) {
	runApplication<SudokuVerifierApplication>(*args)
}

package com.ubs.machacek.sudokuverifier.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

/**
 * Enables to configure the service from 'outside', like using system environment variables
 * in host machine (Spring automatically tries to map them on its own properties).
 * Handler to handle validation exception (i.e. blank delimiter) is not implemented since its
 * outside of scope.
 */
@ConfigurationProperties(prefix = "sudoku.parser")
@Validated
class FileParserConfiguration {
    @get:NotBlank
    var delimiter: String = ""
}
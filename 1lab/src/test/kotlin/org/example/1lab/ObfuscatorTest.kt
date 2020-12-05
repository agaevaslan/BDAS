package org.example.`1lab`

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

class ObfuscatorTest {
    private val input = """
        <employees>
            <employee id="111">
                <firstName>Lokesh</firstName>
                <lastName>Gupta</lastName>
                <location>India</location>
            </employee>
            <employee id="222">
                <firstName>Alex</firstName>
                <lastName>Gussin</lastName>
                <location>Russia</location>
            </employee>
            <employee id="333">
                <firstName>David</firstName>
                <lastName>Feezor</lastName>
                <location>USA</location>
            </employee>
        </employees>""".trimIndent()

    val output = """
        <employees>
            <employee id="rrr">
                <firstName>Cea1i7</firstName>
                <lastName>SkfjJ</lastName>
                <location>XdILJ</location>
            </employee>
            <employee id="sss">
                <firstName>Qb1n</firstName>
                <lastName>SkiiLd</lastName>
                <location>9kiiLJ</location>
            </employee>
            <employee id="ttt">
                <firstName>8JlLI</firstName>
                <lastName>W11peh</lastName>
                <location>YGQ</location>
            </employee>
        </employees>""".trimIndent()

    @Test
    fun testObfuscator() {
        val inputPath = "./input.xml"
        val outputPath = "./output.xml"
        FileWriter(inputPath, StandardCharsets.UTF_8).use {
            it.write(input)
        }


        val args = arrayOf(inputPath, outputPath, "obf")
        main(args)

        FileReader(outputPath).use {
            assertEquals(output, it.readText())
        }
    }

    @Test
    fun testUnobfuscator() {
        val inputPath = "./input.xml"
        val outputPath = "./output.xml"
        FileWriter(inputPath, StandardCharsets.UTF_8).use {
            it.write(output)
        }


        val args = arrayOf(inputPath, outputPath, "d")
        main(args)

        FileReader(outputPath).use {
            assertEquals(input, it.readText())
        }
    }
}

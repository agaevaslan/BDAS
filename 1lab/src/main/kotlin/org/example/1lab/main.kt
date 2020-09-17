@file:JvmName("Main")

package org.example.`1lab`

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Incorrect number of arguments")
        return
    }

    val inPath = args[0]
    val outPath = args[1]
    val mode = args[2]

    val obfuscator = Obfuscator(inPath, outPath, mode)
    obfuscator.process()
}




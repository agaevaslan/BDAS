@file:JvmName("Main")

package org.bdas.`2lab`

fun main() {
    val encryptor = Encryptor()
    println("///////// Check encryption")
    encryptor.testEncryption()
    println("///////// Check testSign")
    encryptor.testSign()
}




package org.bdas.`2lab`

import org.junit.Assert.*
import org.junit.Test
import java.nio.charset.StandardCharsets


class MainTest {

    @Test
    fun testEncryption() {
        val msg = "Message"

        val encryptor = Encryptor()

        val encryptedData = encryptor.encryptData(msg.toByteArray(StandardCharsets.UTF_8), encryptor.certificate)
        val decryptedData = encryptor.decryptData(encryptedData, encryptor.privateKey)

        assertEquals(msg, decryptedData!!.toString(StandardCharsets.UTF_8))
    }

    @Test
    fun testSign() {
        val msg = "Message is there"
        val encryptor = Encryptor()

        val signedData = encryptor.signData(msg.toByteArray(StandardCharsets.UTF_8), encryptor.certificate, encryptor.privateKey)
        assertTrue(encryptor.verifySignedData(signedData!!))
    }
}
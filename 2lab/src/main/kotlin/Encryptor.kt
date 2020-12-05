package org.bdas.`2lab`

import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.cms.ContentInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaCertStore
import org.bouncycastle.cms.*
import org.bouncycastle.cms.jcajce.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
import org.bouncycastle.util.Selector
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.properties.Delegates


class Encryptor() {
    var certificate: X509Certificate by Delegates.notNull()
    var privateKey: PrivateKey by Delegates.notNull()

    init {
        try {
            Security.addProvider(BouncyCastleProvider())
            val certFactory: CertificateFactory = CertificateFactory
                .getInstance("X.509", "BC")

            this.certificate = certFactory
                .generateCertificate(FileInputStream("src/main/resources/labs/public.cer")) as X509Certificate

            val keystorePassword = "password".toCharArray()
            val keyPassword = "password".toCharArray()

            val keystore = KeyStore.getInstance("PKCS12")
            keystore.load(FileInputStream("src/main/resources/labs/private.p12"), keystorePassword)
            this.privateKey = keystore.getKey(
                "baeldung",
                keyPassword
            ) as PrivateKey
        } catch (e: Exception) {
            println(e)
        }
    }

    fun encryptData(data: ByteArray?, encryptionCertificate: X509Certificate?): ByteArray? {
        var encryptedData: ByteArray? = null;
        if (null != data && null != encryptionCertificate) {
            val cmsEnvelopedDataGenerator = CMSEnvelopedDataGenerator();

            val jceKey = JceKeyTransRecipientInfoGenerator(encryptionCertificate);
            cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
            val msg = CMSProcessableByteArray(data);
            val encryptor = JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                .setProvider("BC").build();
            val cmsEnvelopedData = cmsEnvelopedDataGenerator
                .generate(msg, encryptor);
            encryptedData = cmsEnvelopedData.getEncoded();
        }
        return encryptedData;
    }

    fun decryptData(encryptedData: ByteArray?, decryptionKey: PrivateKey?): ByteArray? {
        val decryptedData: ByteArray? = null
        if (null != encryptedData && null != decryptionKey) {
            val envelopedData = CMSEnvelopedData(encryptedData)
            val recipients = envelopedData.recipientInfos.recipients
            val recipientInfo = recipients.iterator().next() as KeyTransRecipientInformation
            val recipient: JceKeyTransRecipient = JceKeyTransEnvelopedRecipient(decryptionKey)
            return recipientInfo.getContent(recipient)
        }
        return decryptedData
    }

    fun testEncryption() {
        try {
            val secretMessage = "My password is 123456Seven"
            println("Original Message : $secretMessage")
            val stringToEncrypt = secretMessage.toByteArray()
            val encryptedData = encryptData(stringToEncrypt, certificate)
            println("Encrypted Message : \n" + String(encryptedData!!))
            val rawData = decryptData(encryptedData, privateKey)
            val decryptedMessage = String(rawData!!)
            println("Decrypted Message : $decryptedMessage")
        } catch (e: Exception) {
            println(e)
        }
    }

    fun signData(data: ByteArray?, signingCertificate: X509Certificate, signingKey: PrivateKey?): ByteArray? {
        var signedMessage: ByteArray? = null;
        val certList = ArrayList<X509Certificate>();
        val cmsData = CMSProcessableByteArray(data);
        certList.add(signingCertificate);
        val certs = JcaCertStore(certList);
        val cmsGenerator = CMSSignedDataGenerator();
        val contentSigner = JcaContentSignerBuilder("SHA256withRSA").build(signingKey);
        cmsGenerator.addSignerInfoGenerator(
            JcaSignerInfoGeneratorBuilder(
                JcaDigestCalculatorProviderBuilder().setProvider("BC")
                    .build()
            ).build(contentSigner, signingCertificate)
        );
        cmsGenerator.addCertificates(certs);

        val cms = cmsGenerator.generate(cmsData, true);
        signedMessage = cms.encoded;
        return signedMessage;
    }

    fun verifySignedData(signedData: ByteArray?): Boolean {
        val inputStream = ByteArrayInputStream(signedData)
        val asnInputStream = ASN1InputStream(inputStream)
        val cmsSignedData = CMSSignedData(
            ContentInfo.getInstance(asnInputStream.readObject())
        )

        val signers = cmsSignedData.signerInfos
        val signer = signers.signers.iterator().next()
        val certCollection : Collection<X509CertificateHolder> = cmsSignedData.certificates.getMatches(signer.sid as Selector<X509CertificateHolder>)
        val certHolder = certCollection.iterator().next()

        return signer.verify(
            JcaSimpleSignerInfoVerifierBuilder()
                .build(certHolder)
        )
    }

    fun testSign() {
        val rawData = "My password is 123456Seven".toByteArray()
        val signedData = signData(rawData, certificate, privateKey)
        val check = verifySignedData(signedData)
        println("Checked: $check")
    }
}


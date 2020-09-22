@file:JvmName("Main")
@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package org.bdas.lab3

import org.apache.http.client.HttpClient
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.security.KeyStore
import java.util.*


@SpringBootApplication
open class MainClass {
    @Bean
    open fun getRestTemplate(): RestTemplate? {
        val restTemplate = RestTemplate()
        val keyStore: KeyStore
        var requestFactory: HttpComponentsClientHttpRequestFactory? = null
        try {
            keyStore = KeyStore.getInstance("jks")
            val classPathResource = ClassPathResource("gateway.jks")
            val inputStream = classPathResource.inputStream
            keyStore.load(inputStream, "aslan123".toCharArray())
            val socketFactory = SSLConnectionSocketFactory(
                SSLContextBuilder()
                    .loadTrustMaterial(null, TrustSelfSignedStrategy())
                    .loadKeyMaterial(keyStore, "aslan123".toCharArray()).build(),
                NoopHostnameVerifier.INSTANCE
            )
            val httpClient: HttpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
                .setMaxConnTotal(5)
                .setMaxConnPerRoute(5)
                .build()
            requestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
            requestFactory.setReadTimeout(10000)
            requestFactory.setConnectTimeout(10000)
            restTemplate.requestFactory = requestFactory
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return restTemplate
    }

}

fun main(args: Array<String>) {
    runApplication<MainClass>(*args)
}


@RestController
@RequestMapping(value = ["/gateway"])
class GatewayController {
    @Autowired
    var restTemplate: RestTemplate? = null

    @Autowired
    var env: Environment? = null

    @get:RequestMapping(value = ["/data"], method = [RequestMethod.GET])
    val data: String
        get() {
            println("Returning data from gateway")
            return "Hello from gateway"
        }

    @get:RequestMapping(value = ["/server-data"], method = [RequestMethod.GET])
    val serverData: String
        get() {
            println("Returning data from serer through gateway")
            try {
                val msEndpoint: String = env!!.getProperty("endpoint.server")
                println("Endpoint name : [$msEndpoint]")
                return restTemplate!!.getForObject(URI(Objects.requireNonNull(msEndpoint)), String::class.java)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
            return "Exception occurred"
        }
}
@file:JvmName("Main")

package org.bdas.lab3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
open class MainClass

fun main(args: Array<String>) {
    runApplication<MainClass>(*args)
}

@RestController
@RequestMapping(value = ["/server"])
class ServerController {
    @get:RequestMapping(value = ["/data"], method = [RequestMethod.GET])
    val data: String
        get() {
            println("Returning data from server")
            return "Hello from server"
        }
}
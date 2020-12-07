@file:JvmName("Main")

package org.bdas.lab4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

val randomString = (1..10)
        .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("");


@SpringBootApplication
open class MainClass

fun main(args: Array<String>) {
    runApplication<MainClass>(*args)
}

@RestController
class DataObjectController {
    @RequestMapping(value = ["/data/{id}"], method = [RequestMethod.GET])
    @ResponseBody
    fun findById(@PathVariable id: Long,
                 req: HttpServletRequest,
                 res: HttpServletResponse): DataObject {
        if (req.getHeader("Test") != null) {
            res.addHeader("Test", req.getHeader("Test"));
        }
        return DataObject(id, randomString)
    }
}

class DataObject(id: Long, name: String) {
    private var id: Long = id
    private var name: String = name

    fun getId(): Long {
        return this.id
    }

    fun setId(id: Long) {
        this.id = id;
    }

    fun getName(): String {
        return this.name
    }

    fun setName(id: String) {
        this.name = name;
    }

}

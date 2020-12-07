package org.bdas.lab4_client

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.stereotype.Component


@EnableZuulProxy
@SpringBootApplication
class Lab4ClientApplication {
	fun main(args: Array<String>) {
		SpringApplication.run(Lab4ClientApplication::class.java, *args)
	}
}

fun main(args: Array<String>) {
	runApplication<Lab4ClientApplication>(*args)
}

@Component
class CustomZuulFilter : ZuulFilter() {
	override fun run(): Any? {
		val ctx: RequestContext = RequestContext.getCurrentContext()
		ctx.addZuulRequestHeader("Test", "TestSample")
		return null
	}

	override fun shouldFilter(): Boolean {
		return true
	}

	override fun filterType(): String {
		return "pre"
	}

	override fun filterOrder(): Int {
		return 0
	}
}


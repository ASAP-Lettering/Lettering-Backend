package com.asap.app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class AppApplication {

}

fun main(args: Array<String>) {
    SpringApplication.run(AppApplication::class.java, *args)
}

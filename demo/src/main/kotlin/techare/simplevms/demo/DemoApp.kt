package techare.simplevms.demo

import com.github.ajalt.clikt.core.CliktCommand

fun main(args: Array<String>) {
    object : CliktCommand() {
        init {
            main(args)
        }
        override fun run() {
        }
    }
}
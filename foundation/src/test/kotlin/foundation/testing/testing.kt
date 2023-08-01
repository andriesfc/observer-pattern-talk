package foundation.testing

inline fun <reified T> T.debug(title: String? = null): T {

    if (title != null) {
        print(title)
        print(": ")
    }

    when (this) {
        is Throwable -> {
            println(message)
            printStackTrace()
        }
        is Map<*,*> -> println(entries.joinToString(prefix = "{", postfix = "}", separator = ", ") { (k,v) -> "$k=$v" })
        is Set<*> -> println(joinToString(prefix = "{", postfix = "}", separator = ", "))
        is List<*> -> println(joinToString(prefix = "[", postfix = "]", separator = ", "))
        is Collection<*> ->  println(joinToString(prefix = "[", postfix = "]", separator = ", "))
        is Array<*> -> println(joinToString(prefix = "[", postfix = "]", separator = ", "))
        else -> println(this)
    }

    return this
}
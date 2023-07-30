package techare.simplevms.demo.debug


fun <T> T.debug(title: String? = null): T = also {
    println(
        when (title) {
            null -> this
            else -> "$title: $this"
        }
    )
}
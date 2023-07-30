package foundation.utils

import java.util.*

/**
 * T
 *
 * @param T
 * @param truth
 * @param notTrue
 * @return
 */
fun <T> Boolean.t(truth: T, notTrue: T): T = if (this) truth else notTrue


val Boolean.yn: String get() = t("yes", "no")

val Boolean.int: Int get() = t(1, 0)
val Int.bool: Boolean
    get() {
        return when {
            this <= 0 -> false
            else -> true
        }
    }


/**
 * Map
 *
 * @param T
 * @param J
 * @param map
 * @receiver
 * @return
 */
fun <T, J> Iterator<T>.map(map: (T) -> J): Iterator<J> = MappingIter(this, map)

private class MappingIter<T, J>(private val from: Iterator<T>, private val map: (T) -> J) : Iterator<J> {
    override fun hasNext(): Boolean = from.hasNext()
    override fun next(): J = map(from.next())
    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other !is MappingIter<*, *> -> false
        else -> from == other.from && map == other.map
    }

    override fun hashCode(): Int = Objects.hash(from, map)
}

/**
 * Each remaining
 *
 * @param T
 * @param action
 * @receiver
 */
inline fun <T> MutableIterator<T>.eachRemaining(crossinline action: MutableIterator<T>.(T) -> Unit) {
    forEachRemaining { action(it) }
}

/**
 * Raw string style
 *
 * @constructor Create empty Raw string style
 */
sealed interface RawStringStyle {
    data object Indent : RawStringStyle

    data object Applied : RawStringStyle

    /**
     * Margin
     *
     * @constructor Create empty Margin
     */
    sealed interface Margin : RawStringStyle {
        val margin: String

        private class MarginImpl(override val margin: String) : Margin {
            override fun equals(other: Any?): Boolean {
                return when {
                    other === this -> true
                    other !is MarginImpl -> false
                    else -> margin == other.margin
                }
            }

            override fun hashCode(): Int = Objects.hash(margin)

            override fun toString(): String = "Margin($margin)"
        }

        companion object : Margin by MarginImpl("|") {
            operator fun invoke(value: String): Margin = MarginImpl(value)

        }
    }

}

fun CharRange.toCharArray() = toList().toCharArray()
fun Collection<Char>.string(): String {
    return when {
        isEmpty() -> ""
        else -> buildString(size) { this@string.forEach(::append) }
    }
}

/**
 * Generates a single line from a block of raw text, based on specific style of the block.
 *
 * @param appliedRawStyle The style already applied to this block of raw text. Note  that by
 *  default it chooses [RawStringStyle.Applied], which means the block as already been formatted via
 * the [String.trimMargin], or [String.trimIndent] call.
 *
 * @return A single line of text
 */
@JvmOverloads
fun String.s(appliedRawStyle: RawStringStyle = RawStringStyle.Applied): String {
    return when {
        isEmpty() -> this
        else -> {
            val raw = when (appliedRawStyle) {
                RawStringStyle.Indent -> trimIndent()
                RawStringStyle.Applied -> this
                is RawStringStyle.Margin -> trimMargin(appliedRawStyle.margin)
            }
            raw.lineSequence().map(String::trim).let { lines ->
                buildString(this@s.length) {
                    lines.joinTo(this, separator = " ")
                }
            }
        }
    }
}

fun String.si(): String = s(RawStringStyle.Indent)
fun String.sm(): String = s(RawStringStyle.Margin)
fun String.sm(margin: String): String = s(
    when {
        margin == RawStringStyle.Margin.margin -> RawStringStyle.Margin
        else -> RawStringStyle.Margin(margin)
    }
)


fun q(string: String): String = buildString(2 + string.length) { append("\"").append(string).append("\"") }
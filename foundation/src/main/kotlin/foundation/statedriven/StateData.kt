package foundation.statedriven

import java.util.*

class StateData<S>(
    private val recording: Boolean = true,
) : MutableIterable<StateData.Entry<S>> {
    sealed interface Entry<S> {
        val state: S
        val data: Any
        val type: Class<out Any>
    }

    private val values = mutableListOf<EntryImpl<S>>()
    val latest: Entry<S> get() = values.last()
    val size: Int get() = values.size
    fun isEmpty(): Boolean = values.isNotEmpty()
    fun isNotEmpty(): Boolean = values.isNotEmpty()
    val latestOrNull: Entry<S>?
        get() = values.lastOrNull()

    override fun hashCode(): Int = Objects.hash(recording, values)
    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is StateData<*> -> false
            else -> recording == other.recording && values == other.values
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <D : Any> set(
        state: S,
        dataType: Class<out D>,
        data: D
    ) {
        if (!recording) {
            values.removeLast()
        }
        values.add(EntryImpl(state, dataType as Class<Any>, data))
    }

    override fun iterator(): MutableIterator<Entry<S>> = values.iterator()
    fun clear() = values.clear()
    override fun toString(): String {
        return "StateData(recording=$recording, size=${values.size}, latest=${latestOrNull?.run { toString() } ?: ""})"
    }

    fun discard(): Entry<S>? {
        return values.takeUnless { it.isEmpty() }?.removeLast()
    }


    operator fun contains(e: Entry<S>): Boolean = e in values

    private data class EntryImpl<S>(
        override val state: S,
        override val type: Class<Any>,
        override val data: Any
    ) : Entry<S> {
        override fun toString(): String {
            return "Entry(state=$state, dataClass=${type.name}, data=$data)"
        }
    }
}

inline operator fun <S, reified D : Any> StateData<S>.set(state: S, data: D) = set(state, D::class.java, data)
operator fun <S> StateData.Entry<S>.component1(): S = state
inline operator fun <reified T> StateData.Entry<*>.component2(): T = data as T
operator fun StateData.Entry<*>.component3(): Class<out Any> = type


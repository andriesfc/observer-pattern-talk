package foundation.observable

interface Observable<out T : Observable<T>> {

    fun attach(observer: Observer<T>): Boolean
    fun detach(observer: Observer<T>): Boolean
    infix fun isObservedBy(observer: Observer<T>): Boolean
    fun detachAll()
    fun isObserved(): Boolean
    fun countObservers():Int
}


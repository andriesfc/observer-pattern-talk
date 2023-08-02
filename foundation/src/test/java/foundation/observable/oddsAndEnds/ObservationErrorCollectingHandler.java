package foundation.observable.oddsAndEnds;

import foundation.observable.Observer;
import foundation.observable.ObserverNotificationFailureAction;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObservationErrorCollectingHandler implements ObserverNotificationFailureAction.Handled {
    private Map<Integer, Pair<Observer<?>, Throwable>> collected = new LinkedHashMap<>();

    @Nullable
    @Override
    public Throwable notifyObserverFailed(@NotNull Observer<?> observer, int index, @NotNull Throwable failure) {
        collected.put(index, new Pair<>(observer, failure));
        return null;
    }

    public Map<Integer, Pair<Observer<?>, Throwable>> drain() {
        var drained = Collections.unmodifiableMap(collected);
        collected = new LinkedHashMap<>();
        return drained;
    }
}

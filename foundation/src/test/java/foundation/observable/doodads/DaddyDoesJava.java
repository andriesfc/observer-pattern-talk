package foundation.observable.doodads;

import foundation.observable.AbstractObservable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class DaddyDoesJava extends AbstractObservable<DaddyDoesJava> {

    private final Random random;
    @NotNull
    private DaddyDoesJava.DoingNow doingNow;

    public DaddyDoesJava(@NotNull DaddyDoesJava.DoingNow initialState) {
        this.doingNow = requireNonNull(initialState);
        this.random = new Random();
    }

    @NotNull
    public DaddyDoesJava.DoingNow doingNow() {
        return doingNow;
    }

    public void doing() {
        DoingNow next;
        do {
            var i = random.nextInt(0, DoingNow.entries().size());
            next = DoingNow.entries().get(i);
        } while (next== doingNow);
        this.doingNow = next;
        notifyObservers();
    }

    public enum DoingNow implements Message {
        WORKING("Working daddy"),
        RESTING("Resting daddy"),
        COOLING("Cooling daddy"),
        BENDING("Bending daddy");
        private final String label;
        DoingNow(@NotNull String label)  {
            this.label = requireNonNull(label);
        }

        @NotNull
        @Override
        public String message() {
            return label;
        }

        private static final List<DoingNow> entries = List.of(DoingNow.values());

        public static List<DoingNow> entries() {
            return entries;
        }
    }

}

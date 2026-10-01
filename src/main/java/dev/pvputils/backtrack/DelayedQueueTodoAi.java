package dev.pvputils.backtrack;

import java.util.ArrayDeque;
import java.util.function.Consumer;

/** FIFO queue with monotonic timestamps, independent of Minecraft and wall-clock changes. */
public final class DelayedQueueTodoAi<T> {
    private record Entry<T>(T value, long timestamp) {}
    private final ArrayDeque<Entry<T>> entries = new ArrayDeque<>();
    public void add(T value, long now) { entries.addLast(new Entry<>(value, now)); }
    public int size() { return entries.size(); }
    public boolean isEmpty() { return entries.isEmpty(); }
    public void clear() { entries.clear(); }
    public void drain(long now, long delay, Consumer<T> consumer) {
        while (!entries.isEmpty() && now - entries.peekFirst().timestamp() >= delay) {
            consumer.accept(entries.removeFirst().value());
        }
    }
    public void flush(Consumer<T> consumer) {
        while (!entries.isEmpty()) consumer.accept(entries.removeFirst().value());
    }
}

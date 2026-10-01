package dev.pvputils.backtrack;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
class DelayedQueueTestTodoAi {
    @Test void respectsDelayBoundaryAndOrder() {
        var queue = new DelayedQueueTodoAi<String>(); var out = new ArrayList<String>();
        queue.add("first", 100); queue.add("second", 110);
        queue.drain(199, 100, out::add); assertTrue(out.isEmpty());
        queue.drain(200, 100, out::add); assertEquals(java.util.List.of("first"), out);
        queue.drain(210, 100, out::add); assertEquals(java.util.List.of("first", "second"), out); assertTrue(queue.isEmpty());
    }
    @Test void flushDeliversAndClearDrops() {
        var queue = new DelayedQueueTodoAi<Integer>(); var out = new ArrayList<Integer>();
        queue.add(1, 0); queue.add(2, 1); queue.flush(out::add); assertEquals(java.util.List.of(1,2), out);
        queue.add(3, 2); queue.clear(); queue.flush(out::add); assertEquals(2, out.size());
    }
    @Test void zeroDelayAndLargeMonotonicTime() {
        var queue = new DelayedQueueTodoAi<Integer>(); var out = new ArrayList<Integer>();
        queue.add(1, Long.MAX_VALUE - 100); queue.drain(Long.MAX_VALUE - 100, 0, out::add); assertEquals(java.util.List.of(1), out);
    }
}

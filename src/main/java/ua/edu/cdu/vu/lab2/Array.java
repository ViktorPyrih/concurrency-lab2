package ua.edu.cdu.vu.lab2;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public record Array<E extends Comparable<E>>(E[] array, ExecutorService executorService) {

    private static final int THREAD_THRESHOLD = 50000;

    private class ArrayMinTask implements Callable<ArrayElement> {

        private final int start;
        private final int end;

        private ArrayMinTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public ArrayElement call() {
            return min(start, end);
        }

        private ArrayElement min(int start, int end) {
            return IntStream.range(0, array.length)
                    .mapToObj(i -> new ArrayElement(array[i], i))
                    .skip(start)
                    .limit(end - start)
                    .min(Comparator.naturalOrder()).orElseThrow();
        }
    }

    private class ArrayElement implements Comparable<ArrayElement> {

        private final E value;
        private final int index;

        private ArrayElement(E value, int index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public int compareTo(ArrayElement o) {
            return value.compareTo(o.value);
        }

        @Override
        public String toString() {
            return String.format("A[%d] = %s", index, value);
        }
    }

    public ArrayElement min(boolean parallel) {
        if (parallel) {
            int nThreads = array.length / THREAD_THRESHOLD;
            IntStream.range(0, nThreads)
                    .mapToObj(n -> new ArrayMinTask(n * THREAD_THRESHOLD, (n + 1) * THREAD_THRESHOLD))
                    .map(executorService::submit)
                    .map(this::getUnchecked)
                    .min(Comparator.naturalOrder())
                    .orElseThrow();
        }

        return min();
    }

    public ArrayElement min() {
        return new ArrayMinTask(0, array.length).call();
    }

    private ArrayElement getUnchecked(Future<ArrayElement> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

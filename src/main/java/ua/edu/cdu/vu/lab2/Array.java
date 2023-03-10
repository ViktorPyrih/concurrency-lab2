package ua.edu.cdu.vu.lab2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public record Array<E extends Comparable<E>>(E[] array, ExecutorService executorService) {

    private static final int THREAD_THRESHOLD = 50000;

    private class ArrayMinTask implements Callable<E> {

        private final int start;
        private final int end;

        private ArrayMinTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public E call() {
            return min(start, end);
        }

        private E min(int start, int end) {
            return Arrays.stream(array)
                    .skip(start)
                    .limit(end - start)
                    .min(Comparator.naturalOrder()).orElseThrow();
        }
    }

    public E min(boolean parallel) {
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

    public E min() {
        return new ArrayMinTask(0, array.length).call();
    }

    private E getUnchecked(Future<E> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

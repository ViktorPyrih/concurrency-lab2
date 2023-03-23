package ua.edu.cdu.vu.lab2.array.impl;

import ua.edu.cdu.vu.lab2.array.Array;
import ua.edu.cdu.vu.lab2.array.ArrayElement;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public record ArrayImpl<E extends Comparable<E>>(E[] array, ExecutorService executorService) implements Array<E> {

    private static final int THREAD_THRESHOLD = 50000;

    private class ArrayMinTask implements Callable<ArrayElement<E>> {

        private final int start;
        private final int end;

        private ArrayMinTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public ArrayElement<E> call() {
            return min(start, end);
        }

        private ArrayElement<E> min(int start, int end) {
            return IntStream.range(0, array.length)
                    .mapToObj(i -> new ArrayElement<>(array[i], i))
                    .skip(start)
                    .limit(end - start)
                    .min(Comparator.naturalOrder()).orElseThrow();
        }
    }

    public ArrayElement<E> min(boolean parallel) {
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

    public ArrayElement<E> min() {
        return new ArrayMinTask(0, array.length).call();
    }

    private ArrayElement<E> getUnchecked(Future<ArrayElement<E>> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package ua.edu.cdu.vu.lab2.array.impl;

import ua.edu.cdu.vu.lab2.array.Array;
import ua.edu.cdu.vu.lab2.array.ArrayElement;

import java.util.*;

public record ArrayImpl2<E extends Comparable<E>>(E[] array) implements Array<E> {

    private static final int THREAD_THRESHOLD = 50000;

    private class ArrayMinTask implements Runnable {

        private final int start;
        private final int end;

        private final MinValueCollector minValueCollector;

        private ArrayMinTask(int start, int end, MinValueCollector minValueCollector) {
            this.start = start;
            this.end = end;
            this.minValueCollector = minValueCollector;
        }

        @Override
        public void run() {
            minValueCollector.collect(min(start, end));
        }

        private ArrayElement<E> min(int start, int end) {
            ArrayElement<E> min = new ArrayElement<>(array[start], start);
            for (int i = start + 1; i < end; i++) {
                if (array[i].compareTo(min.value()) < 0) {
                    min = new ArrayElement<>(array[i], i);
                }
            }

            return min;
        }
    }

    private class MinValueCollector {

        private volatile ArrayElement<E> min;

        public synchronized void collect(ArrayElement<E> value) {
            if (Objects.isNull(min) || value.compareTo(min) < 0) {
                min = value;
            }
        }

        public ArrayElement<E> min() {
            return min;
        }
    }

    public ArrayElement<E> min(boolean parallel) {
        if (parallel) {
            MinValueCollector minValueCollector = new MinValueCollector();

            int nThreads = array.length / THREAD_THRESHOLD;
            Thread[] threads = new Thread[nThreads];
            for (int i = 0; i < threads.length; i++) {
                ArrayMinTask minTask = new ArrayMinTask(i * THREAD_THRESHOLD, (i + 1) * THREAD_THRESHOLD, minValueCollector);
                threads[i] = new Thread(minTask);
                threads[i].start();
            }

            join(threads);

            return minValueCollector.min();
        }

        return min();
    }

    private void join(Thread[] threads) {
        for (var thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayElement<E> min() {
        MinValueCollector minValueCollector = new MinValueCollector();
        ArrayMinTask minTask = new ArrayMinTask(0, array.length, minValueCollector);
        minTask.run();

        return minValueCollector.min();
    }
}

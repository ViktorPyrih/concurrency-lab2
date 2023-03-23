package ua.edu.cdu.vu.lab2.array.impl;

import ua.edu.cdu.vu.lab2.array.Array;
import ua.edu.cdu.vu.lab2.array.ArrayElement;

import java.util.*;

public record ArrayImpl2<E extends Comparable<E>>(E[] array) implements Array<E> {

    private static final int THREAD_THRESHOLD = 50000;

    private class ArrayMinTask implements Runnable {

        private final int start;
        private final int end;

        private ArrayElement<E> min;

        private ArrayMinTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            this.min = min(start, end);
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

        public ArrayElement<E> result() {
            return min;
        }
    }

    public ArrayElement<E> min(boolean parallel) {
        if (parallel) {
            int nThreads = array.length / THREAD_THRESHOLD;
            List<ArrayMinTask> tasks = new ArrayList<>(nThreads);
            Thread[] threads = new Thread[nThreads];
            for (int i = 0; i < threads.length; i++) {
                ArrayMinTask minTask = new ArrayMinTask(i * THREAD_THRESHOLD, (i + 1) * THREAD_THRESHOLD);
                tasks.add(minTask);
                threads[i] = new Thread(minTask);
                threads[i].start();
            }

            join(threads);

            return minResult(tasks);
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

    private ArrayElement<E> minResult(List<ArrayMinTask> tasks) {
        return tasks.stream()
                .map(ArrayMinTask::result)
                .min(Comparator.naturalOrder())
                .orElseThrow();
    }

    public ArrayElement<E> min() {
        ArrayMinTask minTask = new ArrayMinTask(0, array.length);
        minTask.run();
        return minTask.result();
    }
}

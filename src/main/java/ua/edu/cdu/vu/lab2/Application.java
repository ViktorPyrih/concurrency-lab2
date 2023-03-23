package ua.edu.cdu.vu.lab2;

import ua.edu.cdu.vu.lab2.array.Array;
import ua.edu.cdu.vu.lab2.array.impl.ArrayImpl;
import ua.edu.cdu.vu.lab2.array.impl.ArrayImpl2;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private static final Random RANDOM = new Random();
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        int arrayLength = readInt();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Array<Integer> array = generateArray(arrayLength, executorService);
        test(array);
        executorService.shutdownNow();

        Array<Integer> array2 = generateArray2(arrayLength);
        test(array2);
    }

    private static void test(Array<Integer> array) {
        System.out.println(array.min());
        System.out.println(array.min(true));
    }

    private static int readInt() {
        return SCANNER.nextInt();
    }

    private static ArrayImpl<Integer> generateArray(int length, ExecutorService executorService) {
        return new ArrayImpl<>(RANDOM.ints(length).boxed().toArray(Integer[]::new), executorService);
    }

    private static ArrayImpl2<Integer> generateArray2(int length) {
        return new ArrayImpl2<>(RANDOM.ints(length).boxed().toArray(Integer[]::new));
    }
}

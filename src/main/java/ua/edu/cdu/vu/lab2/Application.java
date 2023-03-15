package ua.edu.cdu.vu.lab2;

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
        System.out.println(array.min());
        System.out.println(array.min(true));
        executorService.shutdownNow();
    }

    private static int readInt() {
        return SCANNER.nextInt();
    }

    private static Array<Integer> generateArray(int length, ExecutorService executorService) {
        return new Array<>(RANDOM.ints(length).boxed().toArray(Integer[]::new), executorService);
    }
}

package ua.edu.cdu.vu.lab2.array;

public interface Array<E extends Comparable<E>> {

    ArrayElement<E> min();

    ArrayElement<E> min(boolean parallel);
}

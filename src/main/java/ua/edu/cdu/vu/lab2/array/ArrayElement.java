package ua.edu.cdu.vu.lab2.array;

public record ArrayElement<E extends Comparable<E>>(E value, int index) implements Comparable<ArrayElement<E>> {

    @Override
    public int compareTo(ArrayElement<E> o) {
        return value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return String.format("A[%d] = %s", index, value);
    }
}

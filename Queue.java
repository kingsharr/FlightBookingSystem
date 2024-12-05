/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
import java.util.LinkedList;

public class Queue<T> {
    private LinkedList<T> queue;

    public Queue() {
        this.queue = new LinkedList<>();
    }

    public void enqueue(T item) {
        queue.addLast(item);
    }

    public T dequeue() {
        if (!queue.isEmpty()) {
            return queue.removeFirst();
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public LinkedList<T> getQueue() {
        return queue;
    }
}
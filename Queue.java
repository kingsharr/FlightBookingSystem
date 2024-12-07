/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 * queue class using linkedlist to implement it
 */
import java.util.LinkedList;

public class Queue<T> {
    
    private LinkedList<T> queue;

    public Queue() {
        this.queue = new LinkedList<>();
    }

    // enqueue
    public void enqueue(T item) {
        queue.addLast(item);
    }

    // dequeue
    public T dequeue() {
        if (!queue.isEmpty()) {
            return queue.removeFirst();
        }
        return null;
    }

    //check empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    //return queue
    public LinkedList<T> getQueue() {
        return queue;
    }

    //return queue size
    public int size() {
        return queue.size();
    }

    // peek
    public T peek() {
        if (!queue.isEmpty()) {
            return queue.getFirst();
        }
        return null;
    }
}
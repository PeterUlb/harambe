package com.harambe.algorithm;

/**
 * An Exception indicating that the time limit has been reached.
 * To be used in the AI algorithm.
 * Created by Peter on 24.09.2016.
 */
public class OutOfTimeException extends Exception {
    public OutOfTimeException() { super(); }
    public OutOfTimeException(String message) { super(message); }
}

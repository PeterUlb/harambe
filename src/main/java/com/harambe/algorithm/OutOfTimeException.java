package com.harambe.algorithm;

/**
 * An Exception indicating that the time limit has been reached.
 * To be used in the AI algorithm.
 */
public class OutOfTimeException extends Exception {
    public OutOfTimeException() { super(); }
    public OutOfTimeException(String message) { super(message); }
}

package de.uniba.dsg.beverage_store.exception;

public class InsufficientStockException extends Exception {
    public InsufficientStockException() {
        super();
    }

    public InsufficientStockException(String msg) {
        super(msg);
    }
}

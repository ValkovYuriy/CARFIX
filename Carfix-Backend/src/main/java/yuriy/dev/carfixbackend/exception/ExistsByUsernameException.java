package yuriy.dev.carfixbackend.exception;

public class ExistsByUsernameException extends RuntimeException{

    public ExistsByUsernameException(String message) {
        super(message);
    }
}

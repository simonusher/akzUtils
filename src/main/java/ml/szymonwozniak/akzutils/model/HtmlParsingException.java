package ml.szymonwozniak.akzutils.model;

public class HtmlParsingException extends RuntimeException {
    private String message;

    public void setMessage(String text){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

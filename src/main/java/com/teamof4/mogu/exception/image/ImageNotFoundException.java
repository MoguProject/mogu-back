package com.teamof4.mogu.exception.image;

public class ImageNotFoundException extends NullPointerException {

    public ImageNotFoundException() {
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}

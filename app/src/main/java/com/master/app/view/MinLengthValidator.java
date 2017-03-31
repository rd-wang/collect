package com.master.app.view;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class MinLengthValidator extends LengthValidator {

    public MinLengthValidator(String errorMessage, int minLength) {
        super(errorMessage, minLength, EditTextFactory.MAX_LENGTH);
    }
}

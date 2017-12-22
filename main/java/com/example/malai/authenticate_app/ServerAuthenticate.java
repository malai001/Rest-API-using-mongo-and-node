package com.example.malai.authenticate_app;

/**
 * Created by aarumuga on 31-08-2017.
 */

public interface ServerAuthenticate {

    public String userSignUp(final String name, final String email, final String pass, String authType) throws Exception;
    public String userSignIn(final String user, final String pass, String authType) throws Exception;
}

package com.example.malai.final_authenticator;

/**
 * Created by aarumuga on 31-08-2017.
 */

public interface ServerAuthenticate {

    public String userSignUp(final String name, final String email, final String pass, String authType) throws Exception;
    public String userSignIn(final String user, final String pass, String authType) throws Exception;
}

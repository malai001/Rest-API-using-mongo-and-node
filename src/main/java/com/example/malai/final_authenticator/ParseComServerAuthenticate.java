package com.example.malai.final_authenticator;

import android.nfc.Tag;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by aarumuga on 31-08-2017.
 */

public class ParseComServerAuthenticate implements ServerAuthenticate {
    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {
        System.out.println("value is email" +email+' '+pass+' '+name);
        String url1 = "https://api.parse.com/1/users";
        String url = "http://192.168.1.100:3001/users";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

 //        httpPost.addHeader("X-Parse-Application-Id","XUafJTkPikD5XN5HxciweVuSe12gDgk2tzMltOhr");
 //       httpPost.addHeader("X-Parse-REST-API-Key", "8L9yTQ3M86O4iiucwWb4JS7HkxoSKo7ssJqGChWx");
        httpPost.addHeader("Content-Type", "application/json");

        String user = "{\"email\":\"" + email + "\",\"password\":\"" + pass + "\",\"number\":\"415-392-0202\"}";
        System.out.println(user);
        HttpEntity entity = new StringEntity(user);
        httpPost.setEntity(entity);

        String authtoken = null;
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString+' '+response);
//            if (response.getStatusLine().getStatusCode() != 201) {
//                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
//                throw new Exception("Error creating user["+error.code+"] - " + error.error);
//            }


            User createdUser = new Gson().fromJson(responseString, User.class);
            System.out.print(createdUser);
            Log.d("Session Token",createdUser.sessionToken);
            authtoken = createdUser.sessionToken;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return authtoken;
    }

    @Override
    public String userSignIn(String user, String pass, String authType) throws Exception {

        Log.d("Prolife", "userSignIn");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://api.parse.com/1/login";
        String url2 = "http://192.168.1.100:3001/users/"+user;
        System.out.println(url2);
        String query = null;
        try {
            query = String.format("%s=%s&%s=%s", "username", URLEncoder.encode(user, "UTF-8"), "password", pass);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += "?" + query;

        HttpGet httpGet = new HttpGet(url2);

//        httpGet.addHeader("X-Parse-Application-Id", "XUafJTkPikD5XN5HxciweVuSe12gDgk2tzMltOhr");
//        httpGet.addHeader("X-Parse-REST-API-Key", "8L9yTQ3M86O4iiucwWb4JS7HkxoSKo7ssJqGChWx");

        HttpParams params = new BasicHttpParams();
        params.setParameter("username", user);
        params.setParameter("password", pass);
        httpGet.setParams(params);
//        httpGet.getParams().setParameter("username", user).setParameter("password", pass);

        String authtoken = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);

            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);
            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject)parser.parse(responseString);
            System.out.println(json.get("error"));
            if (json.get("error").getAsBoolean()) {
                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
               throw new Exception("Error signing-in ["+error.code+"] - " + error.error);
            }
            System.out.print(json.getAsJsonArray("message").get(0).getAsJsonObject());
            json = json.getAsJsonArray("message").get(0).getAsJsonObject();
            System.out.println(json.get("userPassword"));
            //json = (JsonObject)parser.parse(json.get("message"));

            //JsonArray message = json.getAsJsonArray("message");
            //System.out.println(message);
            String dbpass = json.get("userPassword").toString();
            dbpass =dbpass.replace("\"", "");
            System.out.println(dbpass);
            System.out.println(pass);
            if (pass.equals(dbpass)){
                System.out.println("LoggedIn");
                User loggedUser = new Gson().fromJson(responseString, User.class);
                System.out.print(loggedUser+" "+responseString);
                authtoken = loggedUser.sessionToken;
                return authtoken;
            }
            else {
                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
                throw new Exception("Error signing-in [" + error.code + "] - " + error.error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    return "error";
    }


    private class ParseComError implements Serializable {
        int code;
        String error;
    }
    private class User implements Serializable {

        private String firstName;
        private String lastName;
        private String username;
        private String phone;
        private String objectId;
        public String sessionToken;
        private String gravatarId;
        private String avatarUrl;


        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        public String getGravatarId() {
            return gravatarId;
        }

        public void setGravatarId(String gravatarId) {
            this.gravatarId = gravatarId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

}

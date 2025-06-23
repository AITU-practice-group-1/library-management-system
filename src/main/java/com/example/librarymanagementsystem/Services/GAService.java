package com.example.librarymanagementsystem.Services;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class GAService {
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public GoogleAuthenticatorKey generateCredentials(){
        return gAuth.createCredentials();
    }

    public String getQRUrl(String username, GoogleAuthenticatorKey key){
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("LibrarySystem-admin", username, key);
    }

    public boolean verify(String secret, int code){
        return gAuth.authorize(secret, code);
    }
}

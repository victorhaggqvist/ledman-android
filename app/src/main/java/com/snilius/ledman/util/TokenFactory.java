package com.snilius.ledman.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author victor
 * @since 2/8/15
 */
public class TokenFactory {

    private Credentials credentials;
    private String apikey;

    public TokenFactory(String apikey) {

        this.apikey = apikey;
    }

    public Credentials getCredentials(){
        if (credentials == null)
            credentials = makeCredentials();
        else if((System.currentTimeMillis() - credentials.getTimestamp())>15)
            credentials = makeCredentials();
        return credentials;
    }

    private Credentials makeCredentials() {
        Credentials credentials = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long timestamp = System.currentTimeMillis();
            String salted = apikey+Long.toString(timestamp);
            byte[] hash = digest.digest(salted.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            credentials = new Credentials(hexString.toString(), timestamp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    public class Credentials {
        private String token;
        private Long timestamp;

        private Credentials(String token, Long timestamp) {
            this.token = token;
            this.timestamp = timestamp;
        }

        public String getToken() {
            return token;
        }

        public Long getTimestamp() {
            return timestamp;
        }
    }

}

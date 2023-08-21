package com.test;

import com.github.salrashid123.GcpProcessCredentials.ICredentialParser;


public class GcloudParser implements ICredentialParser{
    public String parse(String in) {
        String tok = in.replace("\n", "");
        return "{\"access_token\": \"" + tok + "\", \"expires_in\": 3600, \"token_type\": \"Bearer\"}";
      }
}


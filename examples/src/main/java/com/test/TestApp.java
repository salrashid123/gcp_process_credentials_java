package com.test;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;



import com.github.salrashid123.GcpProcessCredentials.Credentials;

public class TestApp {
    public static void main(String[] args) {
        TestApp tc = new TestApp();
    }

    public TestApp() {
        try {

            // String[] a = { "/tmp/token.txt"};
            // String[] e = {"foo=bar"};
            // Credentials sourceCredentials =  new Credentials("/usr/bin/cat",a ,e, null);


            String[] a = { "auth", "print-access-token"};
            String[] e = {"foo=bar"};
            GcloudParser gp = new GcloudParser();
            Credentials sourceCredentials =  new Credentials("gcloud",a ,e, gp);            


            // GoogleCredentials sourceCredentials = GoogleCredentials.getApplicationDefault();
            Storage storage_service = StorageOptions.newBuilder().setCredentials(sourceCredentials).build()
                    .getService();
            for (Bucket b : storage_service.list().iterateAll()) {
                System.out.println(b.getName());
            }

        } catch (Exception ex) {
            System.out.println("Error:  " + ex);
        }
    }

}
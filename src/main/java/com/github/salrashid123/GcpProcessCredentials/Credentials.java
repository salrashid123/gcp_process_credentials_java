package com.github.salrashid123.GcpProcessCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public final class Credentials extends OAuth2Credentials {

  private final String command;
  private final String[] args;
  private final String[] env;
  private final ICredentialParser parser;

  public Credentials(String command, String[] args, String[] env, ICredentialParser parser) {
    this.command = command;
    this.args = args;
    this.env = env;
    this.parser = parser;
  }

  @Override
  public AccessToken refreshAccessToken() throws IOException {

    String cmd = this.command + " " + String.join(" ", this.args);

    Process p = Runtime.getRuntime().exec(cmd, this.env);
    try {
      if(!p.waitFor(2L, TimeUnit.SECONDS)) {
        p.destroy(); 
        throw new IOException("Timeout waiting for process");
      }
    } catch (InterruptedException e) {
      throw new IOException("Timeout waiting for process");
    }
    BufferedReader rr = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String resp = rr.lines().collect(Collectors.joining());

    if (this.parser != null ) {
      resp = this.parser.parse(resp);
    }

    TokenResponse tr = null;
    try {
      tr = new Gson().fromJson(rr, TokenResponse.class);
      JsonFactory jsonFactory = new GsonFactory();
      tr = jsonFactory.fromString(resp, TokenResponse.class);
    } catch (JsonSyntaxException e) {
      throw new IOException(e.getMessage());
    } catch (JsonIOException e) {
      throw new IOException(e.getMessage());
    }

    //TimeZone timeZone = TimeZone.getTimeZone("UTC");
    //Calendar calendar = Calendar.getInstance(timeZone);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, tr.getExpiresInSeconds().intValue());

    return new AccessToken(tr.getAccessToken(), calendar.getTime());
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends OAuth2Credentials.Builder {

    private String command;
    private String[] args;
    private String[] env;
    private ICredentialParser parser;

    private Builder() {
    }

    public Builder setCommand(String command) {
      this.command = command;
      return this;
    }

    public Builder setArgs(String[] args) {
      this.args = args;
      return this;
    }

    public Builder setEnv(String[] env) {
      this.env = env;
      return this;
    }

    public Builder setParser(ICredentialParser parser) {
      this.parser = parser;
      return this;
    }

    public Credentials build() {
      return new Credentials(command, args, env, parser);
    }
  }
}
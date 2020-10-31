package org.openchat.domain.users;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class RegistrationData {
    private final String username;
    private final String password;
    private final String about;
    private final String url;

    public RegistrationData(String username, String password, String about) {
        this(username,password,about,"not defined url");
    }

    public RegistrationData(String username, String password, String about,String url) {
        this.username = username;
        this.password = password;
        this.about = about;
        this.url = url;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String about() {
        return about;
    }

    @Override
    public boolean equals(Object other) {
        return reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    public String url() {
        return url;
    }
}
package org.fatmansoft.teach.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FindBackRequest {


    private String email;
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

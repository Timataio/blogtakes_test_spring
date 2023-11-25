package edu.blogtakes.demo.model.fbo;

public class FBOUser {
    private String email;
    private String username;

    private String password;
    private String passwordCopy;

    public FBOUser() {

    }

    public FBOUser(String email, String username, String password, String passwordCopy) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.passwordCopy = passwordCopy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordCopy() {
        return passwordCopy;
    }

    public void setPasswordCopy(String passwordCopy) {
        this.passwordCopy = passwordCopy;
    }
}

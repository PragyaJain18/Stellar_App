package app.pragyajain.firebase_example;

public class User {
    public String username;
    public String email;
    public String password;
    public String publicKey;
    public String privateKey;
    public String pin;
    public String phone;

    User() {
    }


    User(String phone, String username, String email, String password,String pin, String publicKey, String privateKey) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.pin=pin;
        this.publicKey = publicKey;
        this.privateKey = privateKey;

    }
}

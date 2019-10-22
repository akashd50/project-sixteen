package comp3350.project16.objects;

public class User {
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password)
    {
        this.email=email;
        this.username=username;
        this.password=password;
    }

    //getters
    public String getUsername(){return this.username;}
    public String getEmail(){return this.email;}
    public String getPassword(){return this.password;}

    //setters
    public void setUsername(String newUsername){this.username=newUsername;}
    public void setEmail(String newEmail){this.email=newEmail;}
    public void setPassword(String newPassword){this.password=newPassword;}
}

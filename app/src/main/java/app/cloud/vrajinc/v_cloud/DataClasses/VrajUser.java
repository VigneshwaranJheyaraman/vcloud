package app.cloud.vrajinc.v_cloud.DataClasses;

public class VrajUser {
    public String username, my_face,about_me,who_am_i;
    String first_name,last_name,email,auth_token;
    public  VrajUser(String uname,String fname,String lname,String email, String face, String abt, String w_am,String auth_token)
    {
        this.username = uname;
        this.first_name = fname;
        this.last_name = lname;
        this.email = email;
        this.my_face = face;
        this.about_me = abt;
        this.who_am_i = w_am;
        this.auth_token = auth_token;
    }
    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String uname)
    {
        this.username = uname;
    }
    public String getMy_face()
    {
        return this.my_face;
    }
    public void setMy_face(String face)
    {
        this.my_face = face;
    }
    public String getAbout_me()
    {
        return this.about_me;
    }
    public void setAbout_me(String abt)
    {
        this.about_me = abt;
    }
    public String getWho_am_i()
    {
        return this.who_am_i;
    }
    public void setWho_am_i(String wami)
    {
        this.who_am_i = wami;
    }
    public String getFirst_name()
    {
        return this.first_name;
    }
    public void setFirst_name(String fname)
    {
        this.first_name = fname;
    }
    public String getLast_name()
    {
        return this.last_name;
    }
    public void setLast_name(String lname)
    {
        this.last_name = lname;
    }
    public String getEmail()
    {
        return this.email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getAuth_token()
    {
        return  this.auth_token;
    }
    private void setAuth_token(String auth_token){
        this.auth_token = auth_token;
    }
    @Override
    public String toString() {
        String Message = "Aia'o wau'o "+this.getUsername();
        //+this.getFirst_name()+this.getLast_name()+this.getMy_face()+this.getEmail()+this.getAbout_me()+this.getWho_am_i()+this.getAuth_token()
        return Message;
    }
}

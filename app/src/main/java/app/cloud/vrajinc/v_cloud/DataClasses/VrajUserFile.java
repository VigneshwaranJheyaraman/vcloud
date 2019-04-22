package app.cloud.vrajinc.v_cloud.DataClasses;

public class VrajUserFile {
    public  String user;
    String file_name,file_location,uploaded_date;
    public VrajUserFile(String user, String fname,String f_loc,String up_date)
    {
        this.user = user;
        this.file_name = fname;
        this.file_location = f_loc;
        this.uploaded_date = up_date;
    }
    public String getUser()
    {
        return this.user;
    }
    public void setUser(String user)
    {
        this.user = user;
    }
    public String getFile_name()
    {
        return  this.file_name;
    }
    public void setFile_name(String fname)
    {
        this.file_name = fname;
    }
    public String getFile_location()
    {
        return this.file_location;
    }
    public void setFile_location(String floc)
    {
        this.file_location = floc;
    }
    public String getUploaded_date()
    {
        return this.uploaded_date;
    }
    public void setUploaded_date(String date)
    {
        this.uploaded_date = date;
    }

    @Override
    public String toString() {
        String File_info = "This file :"+this.getFile_name()+" was uploaded by "+this.getUser()+"-"+this.getFile_location()+"-"+this.getUploaded_date();
        return File_info;
    }
}

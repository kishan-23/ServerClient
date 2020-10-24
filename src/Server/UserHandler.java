package Server;

import java.net.Socket;
import java.sql.Connection;

public class UserHandler implements Runnable {
    Socket s;
    Connection con;
    int user_no;
    public UserHandler (int user_no, Socket s, Connection con)
    {
        this.con = con;
        this.s = s;
        this.user_no = user_no;
    }

    public void upload(String type,String path,int max_down, int uploaded_by, int time_lim, String comment)
    {

    }

    public void download ( String key )
    {

    }

    public void showlist()
    {

    }

    public void delFile(String key)
    {

    }

    public void logout()
    {
        try{
            s.close();
        }
        catch(Exception e)
        {
            System.out.println("exception: "+e.getMessage());
        }
    }

    @Override
    public void run() {

    }
}

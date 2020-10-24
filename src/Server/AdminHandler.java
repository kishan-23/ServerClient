package Server;

import java.net.Socket;
import java.sql.Connection;

public class AdminHandler implements Runnable {
    Socket s;
    Connection con;
    int user_no;

    public AdminHandler (int user_no, Socket s, Connection con)
    {
        this.con = con;
        this.user_no = user_no;
        this.s = s;
    }

    private void displayFiles()
    {

    }

    private void displayUsers()
    {

    }

    private void delFile(String code)
    {

    }

    private void delUser(int user_no)
    {

    }

    private void addAdmin(int user_no)
    {

    }

    public void remAdmin(int user_no)
    {

    }

    @Override
    public void run() {

    }
}

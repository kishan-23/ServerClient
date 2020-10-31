package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server {
    public static void main(String[] args) throws IOException, SQLException {

        Connection con = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try{
            //Database
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/share-nowdb","root","root");
            System.out.println("..Database connection established..");
        }
        catch (Exception e)
        {
            System.out.println("Exception in database connection: "+e.getMessage());
            e.printStackTrace();
        }

        ServerSocket ss = new ServerSocket(5128);
        System.out.println("Server.Server Created");
        while(true)
        {
            System.out.println("Waiting for client..");
            Socket s = ss.accept();
            System.out.println("User.Client Arrived :)");
            UserHandler handler = new UserHandler(s,con);
            Thread thread = new Thread(handler);
            thread.start();
        }
    }



    public void sendEcho(String path,DataOutputStream dout)
    {
        int len=0;
        File f = null;
        FileInputStream fin = null;
        try{
            f = new File(path);
            len = (int) f.length();

            fin = new FileInputStream(f);

            byte[] b2=new byte[len];

            fin.read(b2);
            dout.writeInt(len);
            dout.flush();
            System.out.println("every thing is well");
            dout.write(b2);
            dout.flush();
            System.out.println("Echo sent.");

            fin.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        //fout.close();
    }
}

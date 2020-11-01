import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import com.mysql.jdbc.Driver;

public class Server {
    public static void main(String[] args) throws IOException {
        try{
            //Database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/share-nowdb","root","root");
            System.out.println("..Database connection established..");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Exception: ");
        }

        ServerSocket ss = new ServerSocket(5128);
        System.out.println("Server Created");
        System.out.println("Waiting for client..");
        Socket s = ss.accept();
        System.out.println("Client Arrived :)");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try{
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            DataInputStream din = new DataInputStream(s.getInputStream());
            int len = 0;
            String ext = din.readUTF();
            len=din.readInt();
            byte[] b=new byte[len];
            din.readFully(b);

            System.out.println("File Recieved..");
            File f = new File("C:\\Users\\Kishan Verma\\MyServer\\@"+ext+"\\");
            FileOutputStream fout = new FileOutputStream(f);
            fout.write(b,0,len);

            String str = "C:\\Users\\Kishan Verma\\MyServer\\@"+ext+"\\";

            System.out.println("File written");

            //String echo = din.readUTF();
            new Server().sendEcho(str,dout);
           
            din.close();
            s.close();
        }
        catch (IOException i)
        {
            System.out.println(i.getMessage());
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

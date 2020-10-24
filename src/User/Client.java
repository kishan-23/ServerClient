package User;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args)  {
        Socket s=null;
        try{

            s = new Socket("localhost",5128);
            System.out.println("Connection Established");
            //FileOutputStream out = new FileOutputStream(s.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            DataInputStream in = new DataInputStream( s.getInputStream() );
            DataOutputStream out = new DataOutputStream( s.getOutputStream() );
            System.out.println("Enter the name of file (with extension):");
            String ext = reader.readLine();
            System.out.println("Enter the Path of file: ");
            String str = reader.readLine();
            out.writeUTF(ext);
            out.flush();


            new Client().sendFile(out,s,str);

            System.out.println("File Sent..");

            System.out.println("Enter the path to recieve echo: ");
            String echo = reader.readLine();
            System.out.println("echo path:"+echo);
            out.writeUTF(echo);
            int len = in.readInt();
            byte[] becho = new byte[len];
            in.read(becho);
            System.out.println("all fine 1");
            File fecho = new File(echo+"@@"+ext+"//");
            fecho.createNewFile();
            FileOutputStream foutEcho = new FileOutputStream(fecho);
            foutEcho.write(becho);
            System.out.println("all fine 2");
            System.out.println("Echo created.");
            System.out.println("press any key to exit");
            reader.read();
            foutEcho.close();

            in.close();
            out.close();
            s.close();
        }
        catch(Exception i)
        {
            System.out.println("exception"+i.getMessage());
        }
    }
    public void sendFile(DataOutputStream out,Socket s, String str) throws IOException
    {
        FileInputStream fin = null;
        try{
            File f = new File(str);
            System.out.println(f.getCanonicalPath());
            fin = new FileInputStream(f);
            int len = (int) f.length();
            byte[] b= new byte[len];
            fin.read(b);
            out.writeInt(len);
            out.write(b);
            fin.close();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            fin.close();
        }
    }
}

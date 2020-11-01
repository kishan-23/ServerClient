package User;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket s=null;
        DataInputStream in = null;
        DataOutputStream out = null;
        try{
            s = new Socket("localhost",5128);
            System.out.println("Connection Established");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            in = new DataInputStream( s.getInputStream() );
            out = new DataOutputStream( s.getOutputStream() );

            (new Client()).welcome(reader,in,out,s);

        }
        catch(Exception i)
        {
            System.out.println("exception in welcome: "+i.getMessage());
            i.printStackTrace();
        }
        /*finally {
            in.close();
            out.close();
            s.close();
        }*/
    }

    private void welcome(BufferedReader reader, DataInputStream in, DataOutputStream out,Socket s) throws IOException {
        System.out.println("Welcome to Share-Now..");
        String choice;
        while(true)
        {
            System.out.println("Login (press L) or Sign Up (press S)");
            choice = reader.readLine();
            if(choice.equals(new String("L")))
            {
                this.login(reader,in,out,s);
                break;
            }

            else if(choice.equals(new String("S")))
            {
                this.signUP(reader,in,out);
                break;
            }

            else{
                System.out.println("INVALID OPTION, PLEASE TRY AGAIN");
                continue;
            }
        }

    }

    private void login(BufferedReader reader,DataInputStream in, DataOutputStream out,Socket s) throws IOException {
        System.out.println("Enter your Credentials to login:");
        System.out.println("Enter UserID: ");
        String userid = reader.readLine();
        System.out.println("Enter Password: ");
        String pass = reader.readLine();
        System.out.println("Login as Admin? (Enter 1 for YES, any other number for NO)");
        int lc = Integer.parseInt(reader.readLine());
        out.writeUTF("LOGIN");
        out.flush();
        out.writeUTF(userid);
        out.flush();
        out.writeUTF(pass);
        out.flush();
        out.writeInt(lc);
        boolean result = in.readBoolean();
        if(result) {
            System.out.println("Login Successful");
            User u = new User(s,userid,lc);
            u.handle();
        }
        else System.out.println("Invalid user id , password or admin access.");
    }

    private void signUP(BufferedReader reader,DataInputStream in, DataOutputStream out) throws IOException {
        System.out.println("Welcome User");
        out.writeUTF("SIGNUP");
        out.flush();
        System.out.println("Enter your name:");
        String name = reader.readLine();
        out.writeUTF(name);
        out.flush();
        boolean temp = false;
        do{
            System.out.println("Enter user ID of your choice:");
            String userID = reader.readLine();
            out.writeUTF(userID);
            out.flush();
            temp = in.readBoolean();
            if(temp) System.out.println("This Username is available. Continue..");
            else System.out.println("This username is occupied, try something else.");
        }
        while(!temp);
        System.out.println("Enter your email:");
        String mail = reader.readLine();
        out.writeUTF(mail);
        out.flush();
        System.out.println("Enter password:");
        String pass = reader.readLine();
        out.writeUTF(pass);
        out.flush();
        System.out.println("Your password is (can't be obtained in future if forgot) "+pass);
        System.out.println(in.readUTF());
        System.out.println(in.readUTF());
    }

}

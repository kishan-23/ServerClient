package User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class User {

    Socket socket =null;
    String user_id = null;
    int adminchoice;
    private DataOutputStream dout = null;
    private DataInputStream din = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public User(Socket s,String user_id,int ad) throws IOException
    {
        this.socket = s;
        dout = new DataOutputStream( s.getOutputStream() );
        din = new DataInputStream( s.getInputStream() );
        this.adminchoice = ad;
    }


    public void upload() throws IOException {
        dout.writeUTF("U");
        dout.flush();
        System.out.println("Enter the file name: ");
        String name = reader.readLine();
        dout.writeUTF(name);
        dout.flush();
        System.out.println("Enter the path: ");
        String path = reader.readLine();
        File f = new File(path);

        System.out.println("Enter the maximum no. of downloads: ");
        int max_download = Integer.parseInt(reader.readLine());
        dout.writeInt(max_download);
        dout.flush();
        System.out.println("Enter the time for which it will be available for download: ");
        System.out.print("hours: ");
        int hr = Integer.parseInt(reader.readLine());
        System.out.print("minutes: ");
        int min = Integer.parseInt(reader.readLine());
        long time = ((60*hr)+min)*60;
        dout.writeLong(time);
        dout.flush();
        System.out.println("Enter a single line comment:");
        String comment = reader.readLine();
        dout.writeUTF(comment);
        dout.flush();
        try {
            FileInputStream fin = new FileInputStream(f);
            byte[] buffer = new byte[10240];
            int length;
            while ((length = fin.read(buffer)) > 0) {
                dout.write(buffer, 0, length);
            }
            dout.flush();
            fin.close();
        }
        catch (Exception e)
        {
            System.out.println("exception in file input stream: "+e.getMessage());
        }
        System.out.println("upload in progress..");
        String key = din.readUTF();
        System.out.println("The key of your file is: "+key);

    }


    private void download() throws IOException {
        dout.writeUTF("D");
        dout.flush();
        System.out.print("Enter the key of file to be downloaded: ");
        String key = reader.readLine();
        dout.writeUTF(key);
        dout.flush();
        String msg = din.readUTF();
        if(msg.equals(new String("OK")))
        {
            System.out.println("hello1");
            String name = din.readUTF();
            System.out.println(name);
            System.out.println("Enter the path of folder at which downloaded file is received: ");
            String dPath = reader.readLine();
            File fl = new File(dPath+"//"+name+"//");
            FileOutputStream fout = new FileOutputStream(fl);
            byte[] buffer = new byte[10240];
            int length;
            while ((length = din.read(buffer)) > 0) {
                fout.write(buffer, 0, length);
                if(length<10240) break;
            }
            //fout.write(b);
            fout.close();
            System.out.println("Download Successful.");
        }
        else{
            System.out.println(msg);
        }
    }


    public void history() throws IOException {
        dout.writeUTF("H");
        dout.flush();
        String file_name = "File Name";
        String upload_date = "Upload Date";
        String upload_time = "Upload Time";
        int downloads;
        long time_lim;
        String key = "File Key";
        String comments = "Comments";
        String temp = String.format("%20s",file_name)+"     "+String.format("%15s",upload_date)+"     "+String.format("%15s",upload_time)+"     "+String.format("%20s",new String("Downloads left"))+"     "+String.format("%20s",new String("Time limit (minutes)"))+"     "+String.format("%20s",new String("File Key"))+"     "+"Comments";
        System.out.println(temp);
        boolean status = true;
        int counter =0;
        while(status)
        {
            try{
                status = din.readBoolean();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if(status)
            {
                counter++;
                file_name=din.readUTF();
                upload_date = din.readUTF();
                upload_time = din.readUTF();
                downloads = din.readInt();
                key = din.readUTF();
                time_lim = din.readLong();
                comments = din.readUTF();
                temp = String.format("%20s",file_name)+"     "+String.format("%15s",upload_date)+"     "+String.format("%15s",upload_time)+"     "+String.format("%20d",downloads)+"     "+String.format("%20d",time_lim)+"     "+String.format("%20s",key)+"   "+comments;
                System.out.println(temp);
            }
            else break;
        }
        System.out.println("\nFound "+counter+" files.\n");
        System.out.print("Do you want to delete any file? (enter 1 for yes): ");
        int chdel = Integer.parseInt(reader.readLine());
        dout.writeInt(1);
        dout.flush();
        if(chdel==1)
        {
            System.out.print("Enter the file key of file to be deleted: ");
            key = reader.readLine();
            this.delFile(key);
        }
        else{
            System.out.println("continue..");
        }
    }


    private void delFile(String key) throws IOException {
        dout.writeUTF(key);
        dout.flush();
        System.out.println(din.readUTF());
    }

    public void listFilesAdmin() throws IOException {
        dout.writeUTF("LFA");
        dout.flush();
        String file_name = "File Name";
        String upload_date = "Upload Date";
        String upload_time = "Upload Time";
        int downloads;
        long time_lim;
        String key = "File Key";
        String comments = "Comments";
        String temp = String.format("%20s",file_name)+"     "+String.format("%15s",upload_date)+"     "+String.format("%15s",upload_time)+"     "+String.format("%20s",new String("Downloads left"))+"     "+String.format("%20s",new String("Time limit (minutes)"))+"     "+String.format("%20s",new String("File Key"))+"     "+"Comments";
        System.out.println(temp);
        boolean status = true;
        int counter =0;
        while(status)
        {
            try{
                status = din.readBoolean();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if(status)
            {
                counter++;
                file_name=din.readUTF();
                upload_date = din.readUTF();
                upload_time = din.readUTF();
                downloads = din.readInt();
                key = din.readUTF();
                time_lim = din.readLong();
                comments = din.readUTF();
                temp = String.format("%20s",file_name)+"     "+String.format("%15s",upload_date)+"     "+String.format("%15s",upload_time)+"     "+String.format("%20d",downloads)+"     "+String.format("%20d",time_lim)+"     "+String.format("%20s",key)+"   "+comments;
                System.out.println(temp);
            }
            else break;
        }
        System.out.println("\nFound "+counter+" files.\n");
    }

    public void delFileAdmin() throws IOException {
        dout.writeUTF("DFA");
        dout.flush();
        System.out.print("Enter the file key to be deleted: ");
        String key = reader.readLine();
        dout.writeUTF(key);
        dout.flush();
        System.out.println(din.readUTF());
    }




    public void handle() throws IOException {
        String ch="null";
        Scanner sc = new Scanner(System.in);
        while(!ch.equals(new String("Q")))
        {
            System.out.println("Welcome to the main menu.");
            System.out.println("1. Upload (U)");
            System.out.println("2. Download (D)");
            System.out.println("3. History (H)");
            if(adminchoice==1)
            {
                System.out.println("4.List all files. (L)");
                System.out.println("5. Delete a file. (d)");
                System.out.println("6. Quit. (Q)");
            }
            else
                System.out.println("4. Exit (Q)");
            System.out.println("Enter your choice: ");
            ch = sc.nextLine();
            System.out.println(ch);
            if(ch.equals(new String("U"))) {
                this.upload();
            }
            else if(ch.equals(new String("D")))
            {
                this.download();
            }
            else if(ch.equals(new String("H")))
            {
                this.history();
            }
            else if(ch.equals(new String("Q")))
            {
                break;
            }
            else if(adminchoice==1)
            {
                if(ch.equals(new String("L"))) {
                    this.listFilesAdmin();
                }

                if(ch.equals(new String("d"))) {
                    this.delFileAdmin();
                }
            }
            else{
                System.out.println("Invalid choice, try again");
                continue;
            }

        }



    }

}

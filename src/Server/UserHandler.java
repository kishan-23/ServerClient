package Server;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class UserHandler implements Runnable {
    Socket s;
    Connection con;
    DataInputStream din;
    DataOutputStream dout;
    String user_id;
    int user_no;
    String user_name;
    String mail;
    char isAdmin;

    public UserHandler (Socket s, Connection con) throws IOException, SQLException {
        this.con = con;
        this.s = s;
        din = new DataInputStream(s.getInputStream());
        dout = new DataOutputStream(s.getOutputStream());

    }


    private boolean validateLogin(Connection con, DataInputStream din, DataOutputStream dout,Socket s) throws IOException {
        String userId = din.readUTF();
        String pass = din.readUTF();
        Password p = new Password(pass);
        String str1 = p.getHash();
        String query = "select pass from user_info where user_id ="+"(?)";
        try{
            PreparedStatement ps1 = con.prepareStatement(query);
            ps1.setString(1,userId);
            ResultSet rs1 = ps1.executeQuery();
            if(rs1.next()) {
                String str2 = rs1.getString(1);
                System.out.println(str2);
                if (str2.equals(str1))
                {
                    String qu = "select user_no, user_name, email, is_admin from user_info where user_id = "+"(?)";
                    PreparedStatement ps = null;
                    try {
                        ps = con.prepareStatement(qu);
                    } catch (SQLException throwables) {
                        System.out.println("exception in ps:"+throwables.getMessage());
                    }
                    ps.setString(1,userId);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    this.user_no = rs.getInt(1);
                    this.user_name = rs.getString(2);
                    this.mail = rs.getString(3);
                    this.isAdmin = rs.getString(4).charAt(0);
                    return true;
                }
            }
        }

        catch (SQLException throwables) {
            System.out.println("exception in vadilate login:"+throwables.getMessage());
        }
        return false;
    }

    public static boolean signUpUser(Connection con, DataInputStream din, DataOutputStream dout) throws IOException, SQLException {
        String name = din.readUTF();
        String userID;
        boolean temp = false;
        do{
            userID = din.readUTF();
            String query = "select pass from user_info where user_id ="+"(?)";
            try{
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,userID);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) temp = false;
                else temp=true;
            }

            catch (SQLException throwables) {
                System.out.println("exception:"+throwables.getMessage());
            }

            finally {
                dout.writeBoolean(temp);
                dout.flush();
            }
        }
        while(!temp);

        String mail = din.readUTF();
        String pass = din.readUTF();

        NewUser u = new NewUser(con);
        u.registerUser(name,userID,mail,pass,'N');
        String st = "Name :"+name+"\nUserID :"+userID+"\ne-mail :"+mail;
        dout.writeUTF(st);
        dout.flush();
        return true;
    }

    public void upload() throws IOException, SQLException {
        String name, path, comment, query;
        Date dt = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy#HH:mm:ss");
        String key = ""+user_no+"@"+formatter.format(dt);
        java.sql.Date sqldate = new java.sql.Date(dt.getTime());
        Time sqlTime = new Time(dt.getTime());
        LocalDateTime now = LocalDateTime.now();
        name = din.readUTF();
        int i=0;
        path = "C:\\Users\\Kishan Verma\\MyServer\\@"+i+name+"\\";
        File f = new File(path);
        while(f.exists())
        {
            i++;
            path = "C:\\Users\\Kishan Verma\\MyServer\\@"+i+name+"\\";
            f = new File(path);
        }
        FileOutputStream fout = new FileOutputStream(f);
        int max_download = din.readInt();
        long time = din.readLong();
        comment = din.readUTF();
        byte[] buffer = new byte[10240];
        int length;
        while ((length = din.read(buffer)) > 0) {
            fout.write(buffer, 0, length);
            //System.out.println(""+length);
            if(length<10240) break;
        }
        fout.close();
        System.out.println("i'm here out of loop");

        query = "insert into file_info (file_name, file_path, upload_date, upload_time, max_downloads, uploaded_by, time_limit, file_key, comments) "+"values (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ps = con.prepareStatement(query);
        ps.setString(1,name);
        ps.setString(2,path);
        ps.setDate(3,sqldate);
        ps.setTime(4,sqlTime);
        ps.setInt(5,max_download);
        ps.setInt(6,user_no);
        ps.setLong(7,time);
        ps.setString(8,key);
        ps.setString(9,comment);
        ps.execute();
        System.out.println("File added");
        dout.writeUTF(key);
    }

    public void download () throws IOException, SQLException {
        String key = din.readUTF();
        String name;
        java.util.Date jDate;
        String fPath;
        int maxD;
        long timL;
        String qur1 = "select file_path, max_downloads, upload_date, upload_time, time_limit, file_name from file_info where file_key = "+"(?)";
        PreparedStatement prs1 = con.prepareStatement(qur1);
        prs1.setString(1,key);
        ResultSet res1 = prs1.executeQuery();
        if(res1.next())
        {
            fPath = res1.getString(1);
            maxD = res1.getInt(2);
            jDate = res1.getDate(3);
            //jDate = res1.getTime(4);
            timL = res1.getLong(5);
            name = res1.getString(6);
            long diff = (new Date().getTime()-jDate.getTime())/60000;
            if(diff>timL)
            {
                dout.writeUTF("Time Limit for the download is exceeded.");
                dout.flush();
            }
            else{
                maxD--;
                if(maxD<0)
                {
                    dout.writeUTF("Maximum number of downloads for this file is achieved.");
                    dout.flush();
                }
                else{
                    dout.writeUTF("OK");
                    dout.flush();
                    dout.writeUTF(name);
                    dout.flush();
                    File fil = new File(fPath);
                    FileInputStream fin = new FileInputStream(fil);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fin.read(buffer)) > 0) {
                        dout.write(buffer, 0, length);
                    }
                    dout.flush();
                    fin.close();
                    //dout.writeLong(fil.length());
                    //dout.flush();
                    //dout.write(b);
                    //dout.flush();
                    System.out.println("File sent");
                    String qur2 = "update file_info set max_downloads ="+"(?)"+"where file_key = "+"(?)";
                    PreparedStatement prs2 = con.prepareStatement(qur2);
                    prs2.setInt(1,maxD);
                    prs2.setString(2,key);
                    prs2.executeUpdate();
                }
            }
        }
        else{
            dout.writeUTF("File not found.");
            dout.flush();
        }
    }

    public void history() throws SQLException, IOException {
        String qu = "select file_name, upload_date, upload_time, max_downloads, file_key, time_limit, comments from file_info where "+"uploaded_by = "+"(?)";
        PreparedStatement ps = null;
        ResultSet rs =null;
        try{
            ps = con.prepareStatement(qu);
            ps.setInt(1,user_no);
            rs = ps.executeQuery();
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            System.out.println("Exception in ps,rs: "+e.getMessage());
        }

        while (rs.next())
        {
            dout.writeBoolean(true);
            dout.flush();
            String fname = rs.getString(1);
            dout.writeUTF(fname);
            dout.flush();
            java.sql.Date dt = rs.getDate(2);
            Time tm = rs.getTime(3);
            dout.writeUTF(dt.toString());
            dout.flush();
            dout.writeUTF(tm.toString());
            dout.flush();
            dout.writeInt(rs.getInt(4));
            dout.flush();
            dout.writeUTF(rs.getString(5));
            dout.flush();
            dout.writeLong(rs.getLong(6));
            dout.flush();
            dout.writeUTF(rs.getString(7));
            dout.flush();
        }
        dout.writeBoolean(false);
        dout.flush();
        int delch = din.readInt();
        if(delch==1) this.delFile();
    }

    public void delFile() throws IOException, SQLException {
        String key = din.readUTF();
        String qu1 = "select uploaded_by, file_path from file_info where file_key = "+"(?)";
        PreparedStatement ps1 = con.prepareStatement(qu1);
        ps1.setString(1,key);
        ResultSet rs1 = ps1.executeQuery();
        if(rs1.next())
        {
            int uploader = rs1.getInt(1);
            String fpath = rs1.getString(2);
            if(uploader==user_no)
            {
                String qu2 = "delete from file_info where file_key = "+"(?)";
                try{
                    File fl = new File(fpath);
                    if(fl.exists()){
                        System.out.println(""+fl.delete());
                    }
                }
                catch (Exception i)
                {
                    System.out.println("file not on server folder."+i.getMessage());
                }
                PreparedStatement ps2 = con.prepareStatement(qu2);
                ps2.setString(1,key);
                ps2.executeUpdate();
                dout.writeUTF("Successfully deleted.");
                dout.flush();
            }
            else {
                dout.writeUTF("This file doesn't belong to you.");
                dout.flush();
            }
        }
        else{
            dout.writeUTF("no such file was found");
            dout.flush();
        }
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

        String choice = null;
        try {
            choice = din.readUTF();
            if(choice.equals(new String("LOGIN")))
            {
                boolean result = validateLogin(con,din,dout,s);
                dout.writeBoolean(result);
            }

            else if(choice.equals(new String("SIGNUP")))
            {
                boolean b = signUpUser(con,din,dout);
                if(b) {
                    dout.writeUTF("Registration Successful");
                    dout.flush();
                }
            }

        }

        catch (IOException | SQLException e) {
            System.out.println("exception 1"+e.getMessage());
        }


        try {
            String ch = din.readUTF();
            while (!ch.equals(new String("Q")))
            {
                if(ch.equals(new String("U")))
                {
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

                else{

                }
                ch = din.readUTF();
            }


        } catch (IOException | SQLException e) {
            System.out.println("exception 2: "+e.getMessage());
            e.printStackTrace();;
        }

        try {
            din.close();
            dout.close();
            s.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}

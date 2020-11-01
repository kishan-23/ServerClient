package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;

public class AdminHandler implements Runnable {
    Socket s;
    Connection con;
    int user_no;
    DataOutputStream dout;
    DataInputStream din;

    public AdminHandler (int user_no, Socket s, Connection con) throws IOException {
        this.con = con;
        this.user_no = user_no;
        this.s = s;
        dout = new DataOutputStream(s.getOutputStream());
        din = new DataInputStream(s.getInputStream());
    }

    public void listFilesAdmin() throws IOException, SQLException {
        String qu = "select file_name, upload_date, upload_time, max_downloads, file_key, time_limit, comments from file_info";
        PreparedStatement ps = null;
        ResultSet rs =null;
        try{
            ps = con.prepareStatement(qu);
            rs = ps.executeQuery();
        }
        catch(Exception e)
        {
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
    }

    private void displayUsers()
    {

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

        else{
            dout.writeUTF("no such file was found");
            dout.flush();
        }
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

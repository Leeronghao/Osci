package lbstest.example.com.oscilloscope;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by SUER on 2018/5/7.
 */

public class ConnectServer {
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String data;
    private ServerSocket serverSocket;
    private ReceiveListener receiveListener;
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public ConnectServer(String address,int port,ReceiveListener receiveListener)throws Exception{
         serverSocket = new ServerSocket(port);
        socket =  serverSocket.accept();
      //  socket = new Socket(address,port);
        this.receiveListener = receiveListener;
    }
    public void close()throws Exception{
        socket.close();
        serverSocket.close();
//        printWriter.close();
//        bufferedReader.close();
    }
    public void sendOrder(String order) throws  IOException{
        printWriter = new PrintWriter(new BufferedWriter (new OutputStreamWriter(socket.getOutputStream())),true);
        printWriter.println(order);
    }
    public void ReceiveData()throws IOException{
           InputStream inputStream = socket.getInputStream();
           if (inputStream!=null) {
               inputStream = socket.getInputStream();
               byte b[] = new byte[128];
               int len;
               len = inputStream.read(b);
                   if(len==128){
                       this.receiveListener.Received(new String(b,0,len));
                       Log.d("ConnectServer.class", "ReceiveData: ");
                   }
           }
    }
}

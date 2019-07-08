import java.io.*;
import java.net.Socket;

/**
 * @program: Telnet2
 * @description: Telnet客户端
 * @author: Cengyuwen
 * @create: 2019-07-07 13:20
 **/

public class Telnet {
    Socket socket;
    public OutputStream serverOutput;
    public InputStream serverInput;
    String host;
    int port;
    static final int DEFAULT_PORT = 23;
    static final byte IAC = (byte)255;
    static final byte DONT = (byte)254;
    static final byte DO = (byte)253;
    static final byte WONT = (byte)252;
    static final byte WILL = (byte)251;
    public Telnet(String host,int port)
    {
        this.host = host;
        this.port = port;
    }
    public Telnet(String host)
    {
        this.host = host;
        this.port = DEFAULT_PORT;
    }
    public void openConnection() throws IOException {
        socket = new Socket(host,port);
        serverOutput = socket.getOutputStream();
        serverInput = new BufferedInputStream(socket.getInputStream());
        //TODO;
    }
    public void main_proc()
    {
        StreamConnector In_socket = new StreamConnector(System.in,serverOutput);
        StreamConnector Out_socket = new StreamConnector(serverInput,System.out);
        //生成线程
        Thread input_thread = new Thread(In_socket);
        Thread out_thread = new Thread(Out_socket);
        input_thread.start();
        out_thread.start();

    }
    public static void negotiation(BufferedInputStream in,OutputStream out) throws IOException {
        byte[] buff = new byte[3];//接受命令的数组
        while(true){
            in.mark(buff.length);

            if(in.available()>=buff.length){
                in.read(buff);

                if(buff[0]!=IAC){//协商结束
                    in.reset();
                    return;
                }else if(buff[1]==DO){
                    //buff[1] = WONT;
                    buff[1] = WILL;
                    out.write(buff);
                }else if(buff[1]==WILL){
                    buff[1]=DO;
                    out.write(buff);
                }
            }
        }
    }
    public static  void main(String[] args) throws IOException {
        Telnet t = null;
        t=new Telnet("127.0.0.1",23);
        t.openConnection();
        t.main_proc();
    }
}
class StreamConnector implements  Runnable{
    InputStream src = null;
    OutputStream dist = null;
    public StreamConnector(InputStream in, OutputStream out)
    {
        src = in;
        dist = out;
    }
    public void run()
    {
        byte[] buff = new byte[1024];
        while(true)
        {
            try {
                int n = src.read(buff);
                if(n>0)
                {
                    dist.write(buff,0,n);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public static void main(String[] args){
        try{
            // determine light or heavy mode for proxy server
            String proxyMode = args[1];
            Params paramsFile = new Params(args[0]);
            ServerSocket serverSocket = new ServerSocket(paramsFile.port);
            System.out.println("listening on port " + serverSocket.getLocalPort());
            while(true) {
                RequestHandler req = new RequestHandler(serverSocket.accept(), paramsFile.words.split(";"), proxyMode);
                req.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestHandler extends Thread{

    Socket client;
    String[] words;
    String proxyMode;

    RequestHandler(Socket client, String[] words, String proxyMode){
        this.client = client;
        this.words = words;
        this.proxyMode = proxyMode;
    }

    // keeps images, scripts and .css when light mode is on
    boolean isFileAllowed(String path){
        if(this.proxyMode.equals("light")){
            if(path.indexOf(".jpg") != -1 || path.indexOf(".css") != -1 || path.indexOf(".img") != -1 || path.indexOf(".js") != -1){
                return false;
            }else{
                return true;
            }
        }

        return true;
    }

    @Override
    public void run(){
        String line, html = "";
        System.out.println("Request Handler:");
        try{
            System.out.println("Socket created");
            OutputStream outputStream = this.client.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);

            InputStream inputStream = this.client.getInputStream();
            String request = "";
            int nextByte;
            while ((nextByte = inputStream.read()) != '\n') {
                if (nextByte != '\r') {
                    request += (char) nextByte;
                }
            }

            System.out.println(request);
            //get request params, method, host and protocol
            String[] params = request.split(" ");
            Pattern URIPattern = Pattern.compile("(https?):\\/\\/([^/:]+)(?::(\\d+))?(.*)");
            Matcher matcher = URIPattern.matcher(params[1]);
            matcher.matches();
            String host = matcher.group(2);// host - burger.pl

            //handle images if light mode on
            if(this.isFileAllowed(params[1])){
                //get burger.pl html
                Socket socket = new Socket(host, 80);
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                //send request
                pw.println("GET " + params[1] + " HTTP/1.1");
                pw.println("Host: " + host);
                pw.println("Connection: close");
                pw.println();

                //get response
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                while ((line = in.readLine()) != null){
//                    html += line + "\\r\\n";
//                }
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                html = in.lines().collect(Collectors.joining(System.lineSeparator()));

                //markup words
                for(int i = 0; i < this.words.length; i++){
                    html = html.replaceAll(this.words[i], "<span style='background: yellow; color: red;'>" + this.words[i] + "</span>");
                }

                //send html back to client
                printWriter.println(html);
                printWriter.flush();
                client.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

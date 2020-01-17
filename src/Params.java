import java.io.BufferedReader;
import java.io.FileReader;

public class Params {
    int port;
    String words;
    String cache_dir;

    Params(String path){
        BufferedReader reader;
        int port = 0;
        String words = "";
        String cache_dir = "";
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                if(line.contains("PROXY_PORT")){
                    port = Integer.parseInt(line.split("=")[1]);
                }else if(line.contains("WORDS")){
                    words = line.split("=")[1];
                }else if(line.contains("CACHE_DIR")){
                    cache_dir = line.split("=")[1];
                }
                line = reader.readLine();
            }

            if(port == 0 || words == "" || cache_dir == ""){
                throw new Exception("Invalid arguments");
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        this.port = port;
        this.words = words;
        this.cache_dir = cache_dir;
    }
}

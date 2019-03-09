package core;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
        	
        	/*JSONObject json = new JSONObject();
        	json.put("type", "CONNECT");
        	Socket s = new Socket("192.168.0.100", 7777);
        	try (OutputStreamWriter out = new OutputStreamWriter(
        	        s.getOutputStream(), StandardCharsets.UTF_8)) {
        	    out.write(json.toString());
        	}*/
        	
        	while(!clientSocket.isClosed()) {
            InputStream input  = clientSocket.getInputStream();
            
            //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //String content = br.readLine();
            //System.out.println(content);
            
            //if (input.available() > 0) {
            //	System.out.println(input.read());
            //}
            
            
            //OutputStream output = clientSocket.getOutputStream();
            //long time = System.currentTimeMillis();
            //output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " + this.serverText + " - " + time + "").getBytes());
            //output.close();
            //input.close();
            //System.out.println("Request processed: " + time);
        	}
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
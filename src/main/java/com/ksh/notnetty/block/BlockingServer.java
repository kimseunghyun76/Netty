package com.ksh.notnetty.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Helloworld
 * User : USER
 * Date : 2015-11-11
 * Time : 오전 11:43
 * To change this template use File | Settings | File and Code Templates.
 */
public class BlockingServer {
    static Logger logger = LoggerFactory.getLogger(BlockingServer.class);

    public static void main(String[] args) throws Exception{
        BlockingServer server = new BlockingServer();
        server.run();
    }
    private void run() throws IOException{
        ServerSocket serverSocket = new ServerSocket(8888);
        logger.info("접속 대기중");

        while (true){
            Socket socket = serverSocket.accept();
            logger.info("클라이언트 연결됨");

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            while(true){
                try{
                    int request = inputStream.read();
                    outputStream.write(request);
                }catch(IOException e){
                    break;
                }

            }
        }
    }
}

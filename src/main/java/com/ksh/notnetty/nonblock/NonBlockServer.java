package com.ksh.notnetty.nonblock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by Helloworld
 * User : USER
 * Date : 2015-11-11
 * Time : 오후 12:11
 * To change this template use File | Settings | File and Code Templates.
 */
public class NonBlockServer {

    static Logger logger = LoggerFactory.getLogger(NonBlockServer.class);
    private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
    private ByteBuffer byteBuffer = ByteBuffer.allocate(2 * 1024);

    private void startEchoServer(){
        try(
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
        ){
            if((serverSocketChannel.isOpen()) && (selector.isOpen())){
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(8888));

                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                logger.info("접속 대기중");

                while(true){
                    selector.select();
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                    while(keys.hasNext()){
                        SelectionKey key = (SelectionKey) keys.next();
                        keys.remove();

                        if(!key.isValid()){
                            continue;
                        }

                        if(key.isAcceptable()){
                            this.acceptOP(key,selector);
                        }else if(key.isReadable()){
                            this.readOP(key);
                        }else if(key.isWritable()){
                            this.writeOP(key);
                        }
                    }
                }
            }else{
                logger.info("서버 소켓을 생성하지 못했습니다.");
            }

        }catch (IOException ex){
            logger.debug(ex.toString());
        }
    }

    private void writeOP(SelectionKey key) throws IOException{
        SocketChannel socketChannel  = (SocketChannel)key.channel();

        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        Iterator<byte[]> its = channelData.iterator();

        while(its.hasNext()){
            byte[] it = its.next();
            its.remove();
            socketChannel.write(ByteBuffer.wrap(it));
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void readOP(SelectionKey key) {
        try{
            SocketChannel socketChannel = (SocketChannel)key.channel();
            byteBuffer.clear();
            int numRead = -1;
            try{
                numRead = socketChannel.read(byteBuffer);
            }catch (IOException e){
                logger.debug("데이터 읽기 에러!");
            }

            if(numRead == -1){
                this.keepDataTrack.remove(socketChannel);
                logger.info("클라이언트 연결 종료 : {}",socketChannel.getRemoteAddress());
                socketChannel.close();
                key.channel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(byteBuffer.array(), 0 , data, 0 , numRead);
            logger.info("{} from {}",new String(data,"UTF-8") ,socketChannel.getRemoteAddress());
            doEchoJob(key, data);
        }catch (IOException ex){
            logger.debug(ex.toString());
        }
    }

    private void doEchoJob(SelectionKey key, byte[] data) {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.add(data);

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void acceptOP(SelectionKey key, Selector selector) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        logger.info("클라이언트 연결됨 : {}", socketChannel.getRemoteAddress());

        keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) {
        NonBlockServer nonBlockServer = new NonBlockServer();
        nonBlockServer.startEchoServer();
    }
}

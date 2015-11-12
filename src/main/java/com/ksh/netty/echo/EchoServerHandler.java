package com.ksh.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by Helloworld
 * User : USER
 * Date : 2015-11-12
 * Time : 오전 11:48
 * To change this template use File | Settings | File and Code Templates.
 */
public class EchoServerHandler  extends ChannelInboundHandlerAdapter {

    static Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
        logger.info("들어온 데이터 {}","1");
        logger.info(readMessage);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }
}

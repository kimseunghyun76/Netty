package com.ksh.notnetty.discard;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Helloworld
 * User : USER
 * Date : 2015-11-10
 * Time : ���� 4:07
 * To change this template use File | Settings | File and Code Templates.
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

    static Logger logger = LoggerFactory.getLogger(DiscardServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("OK Client send some data");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }
}

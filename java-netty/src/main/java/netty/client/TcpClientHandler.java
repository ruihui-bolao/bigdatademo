package netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/1 13:27
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   netty client 向服务端发送请求的 handle
 */
public class TcpClientHandler extends ChannelInboundHandlerAdapter {

/*    // 读取server返回的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SimpleClientHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        System.out.println("Server said:" + new String(result1));
        result.release();
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        String path="java-netty\\src\\main\\resources\\t1.txt";
        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] msg = new byte[fileInputStream.available()];
        fileInputStream.read(msg);
        System.out.println("发送服务端内容为: " + new String(msg));
        ByteBuf encoded =  Unpooled.buffer(1024);
        encoded.writeBytes(msg);
        ctx.write(encoded);
        ctx.flush();
    }

}

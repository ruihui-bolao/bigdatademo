package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.FileInputStream;
import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/5 16:44
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class UdpClient {

    public void run(int port)throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpClientHandler());


            // 向服务端发送信息
            String path = "java-netty\\src\\main\\resources\\t1.txt";
            FileInputStream fileInputStream = new FileInputStream(path);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            ByteBuf buffer = Unpooled.buffer(1024);   // 文本内容 buffer
            buffer.writeBytes(bytes);
            InetSocketAddress recipient = new InetSocketAddress("192.168.102.38", port);   // IP host 和 port
            Channel ch = b.bind(0).sync().channel();
            ch.writeAndFlush(new DatagramPacket(buffer, recipient)).sync();
            ch.closeFuture().sync();
        }
        finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8889;
        if(args.length>0){
            try{
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        new UdpClient().run(port);
    }

}

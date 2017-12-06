package thread;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import netty.server.UdpServerHandler;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/5 15:46
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class UdpRunnable implements Runnable {

    public int udpPort;
    public String path;

    public UdpRunnable(int udpPort, String path) {
        this.udpPort = udpPort;
        this.path = path;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler(path))//初始化处理器
                    .option(ChannelOption.SO_BROADCAST, true);  // 支持广播
            b.bind(udpPort).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

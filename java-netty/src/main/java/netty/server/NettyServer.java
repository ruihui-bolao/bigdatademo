package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import thread.TcpRunnable;
import thread.UdpRunnable;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/1 11:58
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  netty Server 监听连接
 */
public class NettyServer {

    /**
     * tcp服务端端口号
     */
    public static int tcpPort = 8888;

    /**
     * udp 服务端端口号
     */
    public static int udpPort = 8889;

    /**
     * 文件的保存路径
     */
    public static String path;

    protected static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    /**
     * netty 服务端连接
     *
     * @throws Exception
     */
    public static void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //启动 NIO 服务的辅助启动类
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    //配置 Channel
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 注册handler
                            ch.pipeline().addLast(new TcpServerHandler(path));  //客户端触发的操作
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(tcpPort).sync();
            // 等待服务器 socket 关闭 。
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 多线程同时启动 tcp 和 udp
     *
     * @throws Exception
     */
    public static void runMoreThread() throws Exception {

//        LOG.info("启动多线程 TCP 和 UDP ");
        System.out.println("启动多线程 TCP 和 UDP ");
        // Tcp thread
        TcpRunnable tcpRunnable = new TcpRunnable(tcpPort, path);
        Thread tcpThread = new Thread(tcpRunnable);
        tcpThread.start();

        // udp thread
        UdpRunnable udpRunnable = new UdpRunnable(udpPort, path);
        Thread udpThread = new Thread(udpRunnable);
        udpThread.start();

    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        NettyServer.tcpPort = tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        NettyServer.udpPort = udpPort;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static void main(String[] args) throws Exception {
        NettyServer.runMoreThread();
    }
}

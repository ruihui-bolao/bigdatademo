package netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/5 16:05
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    /**
     * 保存文件的基本路径
     */
    public String basePath;

    protected static final Logger LOG = LoggerFactory.getLogger(UdpServerHandler.class);

    public UdpServerHandler(String path) {
        this.basePath = path;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {

        // 如果输入数据不为空
        if (packet.content().readableBytes() > 0) {

            // 读取收到的数据
            ByteBuf buf = (ByteBuf) packet.copy().content();
            byte[] receiveByte = new byte[buf.readableBytes()];
            buf.readBytes(receiveByte);
            System.out.println("服务端接收到的信息为：" + new String(receiveByte));
//            LOG.info("UDP协议信息处理完毕！");
            System.out.println(DateTime.now().toString() + "-----UDP协议信息处理完毕！");
            buf.release();

            /*            // 回复一条信息给客户端
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("Hello，我是Server，我的时间戳是"+System.currentTimeMillis()
                            , CharsetUtil.UTF_8)
                    , packet.sender())).sync();*/
        }
    }

}

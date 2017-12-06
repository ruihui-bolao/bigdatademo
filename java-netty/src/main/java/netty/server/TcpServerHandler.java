package netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/1 12:05
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    netty server 处理 client 的 handle
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 保存文件的基本路径
     */
    public String basePath;

    protected static final Logger LOG = LoggerFactory.getLogger(TcpServerHandler.class);

    public TcpServerHandler(String path) {
        this.basePath = path;
    }

    /**
     * 接受客户端发过来的数据并进行处理
     *
     * @param ctx
     * @param msg 客户端发送的消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg != null) {

            // 接受客户端发过来的信息
            ByteBuf result = (ByteBuf) msg;
            byte[] receiveByte = new byte[result.readableBytes()];
            result.readBytes(receiveByte);
            System.out.println("服务端接收到的信息为：" + new String(receiveByte));
            LOG.info("TCP协议信息处理完毕！");
            System.out.println(DateTime.now().toString() + "-----TCP协议信息处理完毕！");
            result.release();
        }

        /*        // 向客户端发送消息
        String response = "hello client!";
        // 在当前场景下，发送的数据必须转换成ByteBuf数组
        ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
        encoded.writeBytes(response.getBytes());
        ctx.write(encoded);
        ctx.flush();*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server occur exception:" + cause.getMessage());
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        ctx.flush();
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}

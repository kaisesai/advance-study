package com.liukai.advance.netty.danmu;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * http 请求处理
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  
  private static final File INDEX;
  
  static {
    // 初始化加载弹幕网页
    try {
      URL url = HttpRequestHandler.class.getClassLoader().getResource("WebsocketDanMu.html");
      assert url != null;
      INDEX = new File(url.toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException("unable to loader WebsocketDanMu.html", e);
    }
  }
  
  public String wsUri;
  
  public HttpRequestHandler(String wsUri) {
    this.wsUri = wsUri;
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    // 判断请求是否为 ws 请求连接
    if (StringUtils.equalsIgnoreCase(request.uri(), wsUri)) {
      // 交给 websocket 请求处理器进行处理
      ctx.fireChannelRead(request.retain());
    } else {
      // 给客户端返回弹幕页面
      // 判断请求头状态是否继续保持 100 的状态
      if (HttpUtil.is100ContinueExpected(request)) {
        // 向客户端写入 http1.1 响应，状态码为 100
        ctx.writeAndFlush(
          new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
      }
      
      // 创建随机访问文件
      RandomAccessFile file = new RandomAccessFile(INDEX, "r");
      
      // 设置 http 响应
      DefaultHttpResponse response = new DefaultHttpResponse(request.protocolVersion(),
                                                             HttpResponseStatus.OK);
      // 设置头信息
      response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
      // 保存活跃
      boolean keepAlive = HttpUtil.isKeepAlive(request);
      
      if (keepAlive) {
        // 设置头信息：内容长度、连接继续存活
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      }
      // 写入响应
      ctx.write(response);
      
      if (ctx.pipeline().get(SslHandler.class) == null) {
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
      } else {
        ctx.write(new ChunkedNioFile(file.getChannel()));
      }
      // 写入空的数据
      ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
      if (!keepAlive) {
        //
        channelFuture.addListener(ChannelFutureListener.CLOSE);
      }
      
      // 关闭文件
      file.close();
    }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.out
      .println("client: " + ctx.channel().remoteAddress() + " 异常，message: " + cause.getMessage());
    cause.printStackTrace();
    ctx.close();
  }
  
}

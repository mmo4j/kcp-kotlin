package test;

import com.backblaze.erasure.fec.Snmp;

import io.netty.buffer.ByteBuf;
import kcp.ChannelConfig;
import kcp.KcpListener;
import kcp.KcpServer;
import kcp.Ukcp;

/**
 * mbp 2.3 GHz Intel Core i9 16GRam 单连接 带fec 5W/s qps 单连接 不带fec 8W/s qps
 * Created by JinMiao
 * 2019-06-27.
 */
public class KcpPingPongExampleServer implements KcpListener {

  int i = 0;
  long start = System.currentTimeMillis();

  public static void main(String[] args) {

    KcpPingPongExampleServer kcpRttExampleServer = new KcpPingPongExampleServer();
    ChannelConfig channelConfig = new ChannelConfig();
    channelConfig.nodelay(true, 40, 2, true);
    channelConfig.setSndwnd(1024);
    channelConfig.setRcvwnd(1024);
    channelConfig.setMtu(1400);
    //channelConfig.setFecDataShardCount(10);
    //channelConfig.setFecParityShardCount(3);
    channelConfig.setAckNoDelay(true);
    channelConfig.setCrc32Check(true);
    //channelConfig.setTimeoutMillis(10000);
    KcpServer kcpServer = new KcpServer();
    kcpServer.init(Runtime.getRuntime().availableProcessors(), kcpRttExampleServer, channelConfig, 10001);
  }

  @Override
  public void onConnected(Ukcp ukcp) {
    System.out.println("有连接进来" + Thread.currentThread().getName() + ukcp.user().getRemoteAddress());
  }

  @Override
  public void handleReceive(ByteBuf buf, Ukcp kcp, int protocolType) {
    i++;
    long now = System.currentTimeMillis();
    if (now - start > 1000) {
      System.out.println("收到消息 time: " + (now - start) + "  message :" + i);
      start = now;
      i = 0;
    }
    kcp.writeOrderedReliableMessage(buf);
  }

  @Override
  public void handleException(Throwable ex, Ukcp kcp) {
    ex.printStackTrace();
  }

  @Override
  public void handleClose(Ukcp kcp) {
    System.out.println(Snmp.snmp.toString());
    Snmp.snmp = new Snmp();
    System.out.println("连接断开了");
  }
}

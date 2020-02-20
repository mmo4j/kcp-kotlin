package kcp;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

/**
 * Created by JinMiao
 * 2018/11/2.
 */
public class User {

  private Channel channel;
  private InetSocketAddress remoteAddress;
  private InetSocketAddress localAddress;

  private Object cache;

  public User(Channel channel, InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
    this.channel = channel;
    this.remoteAddress = remoteAddress;
    this.localAddress = localAddress;
  }

  public <T> T getCache() {
    return (T) cache;
  }

  public void setCache(Object cache) {
    this.cache = cache;
  }

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public InetSocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(InetSocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public InetSocketAddress getLocalAddress() {
    return localAddress;
  }

  public void setLocalAddress(InetSocketAddress localAddress) {
    this.localAddress = localAddress;
  }

  @Override
  public String toString() {
    return "User{" +
           "remoteAddress=" + remoteAddress +
           ", localAddress=" + localAddress +
           '}';
  }
}

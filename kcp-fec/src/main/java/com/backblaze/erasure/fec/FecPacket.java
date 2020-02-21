package com.backblaze.erasure.fec;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;

/**
 * Created by JinMiao
 * 2018/6/26.
 */
public class FecPacket {

  private static final Recycler<FecPacket> fecPacketRecycler = new Recycler<FecPacket>() {
    @Override
    protected FecPacket newObject(Handle<FecPacket> handle) {
      return new FecPacket(handle);
    }
  };
  private long seqid;
  private int flag;
  private ByteBuf data;
  private Recycler.Handle<FecPacket> recyclerHandle;

  private FecPacket(Recycler.Handle<FecPacket> recyclerHandle) {
    this.recyclerHandle = recyclerHandle;
  }

  public static FecPacket newFecPacket(ByteBuf byteBuf) {
    FecPacket pkt = fecPacketRecycler.get();
    pkt.seqid = byteBuf.readUnsignedIntLE();
    pkt.flag = byteBuf.readUnsignedShortLE();
    pkt.data = byteBuf.retainedSlice(byteBuf.readerIndex(), byteBuf.capacity() - byteBuf.readerIndex());
    pkt.data.writerIndex(byteBuf.readableBytes());
    return pkt;
  }

  public void release() {
    this.seqid = 0;
    this.flag = 0;
    this.data.release();
    this.data = null;
    recyclerHandle.recycle(this);
  }

  public long getSeqid() {
    return seqid;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }

  public ByteBuf getData() {
    return data;
  }

  public void setData(ByteBuf data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "FecPacket{" +
           "seqid=" + seqid +
           ", flag=" + flag +
           '}';
  }
}

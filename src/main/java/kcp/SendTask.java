package kcp;

import org.jctools.queues.MpscArrayQueue;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;
import threadpool.task.ITask;

/**
 * Created by JinMiao
 * 2018/9/11.
 */
public class SendTask implements ITask {

  private static final Recycler<SendTask> RECYCLER = new Recycler<SendTask>(2 << 16) {
    @Override
    protected SendTask newObject(Handle<SendTask> handle) {
      return new SendTask(handle);
    }
  };
  private final Recycler.Handle<SendTask> recyclerHandle;
  private Ukcp kcp;

  private SendTask(Recycler.Handle<SendTask> recyclerHandle) {
    this.recyclerHandle = recyclerHandle;
  }

  static SendTask New(Ukcp kcp) {
    SendTask sendTask = RECYCLER.get();
    sendTask.kcp = kcp;
    return sendTask;
  }

  @Override
  public void execute() {
    try {
      //查看连接状态
      if (!kcp.isActive()) {
        return;
      }
      //从发送缓冲区到kcp缓冲区
      MpscArrayQueue<ByteBuf> queue = kcp.getSendList();
      while (kcp.canSend(false)) {
        ByteBuf byteBuf = queue.poll();
        if (byteBuf == null) {
          break;
        }
        try {
          this.kcp.send(byteBuf);
          byteBuf.release();
        } catch (IOException e) {
          kcp.getKcpListener().handleException(e, kcp);
          return;
        }
      }
      //如果有发送 则检测时间
      if (!kcp.canSend(false) || (kcp.checkFlush() && kcp.isFastFlush())) {
        long now = System.currentTimeMillis();
        long next = kcp.flush(now);
        kcp.setTsUpdate(now + next);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      release();
    }
  }

  public void release() {
    kcp = null;
    recyclerHandle.recycle(this);
  }


}

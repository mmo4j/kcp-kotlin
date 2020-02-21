package threadpool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;

/**
 * 基于disruptor的线程池
 *
 * @author jinmiao
 * 2014-9-12 上午9:51:09
 */
public class DisruptorExecutorPool {

  private static final Logger log = LoggerFactory.getLogger(DisruptorExecutorPool.class);
  /**
   * 定时线程池
   **/
  //private static final ScheduledThreadPoolExecutor scheduled  = new ScheduledThreadPoolExecutor(1,new TimerThreadFactory());
  private static final DefaultEventLoop EVENT_EXECUTORS = new DefaultEventLoop();
  private static final HashedWheelTimer
    hashedWheelTimer =
    new HashedWheelTimer(new TimerThreadFactory(), 1, TimeUnit.MILLISECONDS);
  protected List<IMessageExecutor> executor = new Vector<>();
  protected AtomicInteger index = new AtomicInteger();

  public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long milliseconds) {
    return EVENT_EXECUTORS.scheduleWithFixedDelay(command, milliseconds, milliseconds, TimeUnit.MILLISECONDS);
  }

  public static void scheduleHashedWheel(TimerTask timerTask, long milliseconds) {
    hashedWheelTimer.newTimeout(timerTask, milliseconds, TimeUnit.MILLISECONDS);
  }

  /**
   * 创造一个线程对象
   */
  public IMessageExecutor createDisruptorProcessor(String threadName) {
    IMessageExecutor singleProcess = new DisruptorSingleExecutor(threadName);
    executor.add(singleProcess);
    singleProcess.start();
    return singleProcess;
  }

  public void stop() {
    for (IMessageExecutor process : executor) {
      process.stop();
    }

    //if(!scheduled.isShutdown())
    //	scheduled.shutdown();

    if (!EVENT_EXECUTORS.isShuttingDown()) {
      EVENT_EXECUTORS.shutdownGracefully();
    }
  }

  /**
   * 从线程池中按算法获得一个线程对象
   */
  public IMessageExecutor getAutoDisruptorProcessor() {
    int index = this.index.incrementAndGet();
    return executor.get(index % executor.size());
  }

  /**
   * 定时器线程工厂
   **/
  private static class TimerThreadFactory implements ThreadFactory {

    private AtomicInteger timeThreadName = new AtomicInteger(0);

    public Thread newThread(Runnable r) {
      return new Thread(r, "TimerThread " + timeThreadName.addAndGet(1));
    }
  }

}

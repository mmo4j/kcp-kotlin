package threadpool.thread;

import com.lmax.disruptor.EventHandler;

public class DistriptorEventHandler implements EventHandler<DistriptorHandler> {

  public void onEvent(DistriptorHandler event, long sequence,
                      boolean endOfBatch) {
    event.execute();
  }
}

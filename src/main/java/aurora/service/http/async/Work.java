/*
 * Created on 2014年12月21日 下午6:25:34
 * $Id$
 */
package aurora.service.http.async;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletResponse;

public class Work implements ServletContextListener {
    private static final BlockingQueue<AsyncContext> queue = new LinkedBlockingQueue<AsyncContext>();

    private volatile Thread thread;
    

    public static void add(AsyncContext c) {
      queue.add(c);
      ExecutorService svc = Executors.newFixedThreadPool(30);
      //
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
      //  System.out.println("context init");
      thread = new Thread(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              Thread.sleep(2000);
              AsyncContext context;
              while ((context = queue.poll()) != null) {
                try {
                  ServletResponse response = context.getResponse();
                  response.setContentType("text/plain");
                  PrintWriter out = response.getWriter();
                  out.printf("Thread %s completed the task", Thread.currentThread().getName());
                  out.flush();
                } catch (Exception e) {
                  throw new RuntimeException(e.getMessage(), e);
                } finally {
                  context.complete();
                }
              }
            } catch (InterruptedException e) {
              return;
            }
          }
        }
      });
      thread.start();
      System.out.println("context init end");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
      thread.interrupt();
    }
  }
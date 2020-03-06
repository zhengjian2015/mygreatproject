package ioooc.wukong.atuomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * automic 基本上都是自旋锁
 * do-while循环
 * 在while里死循环 直到修改成功
 *适用于多核的服务器，在并发度不是特别高 同时临界度比较小时
 * 演示自旋锁
 */
public class SpinLock {

    private AtomicReference<Thread> sign = new AtomicReference<>();

    public void lock(){
        Thread current = Thread.currentThread();
        while (!sign.compareAndSet(null,current)) {
            //把背后的开销打印出来
            System.out.println("自旋获取失败，再次尝试");
        }
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        sign.compareAndSet(current,null);
    }

    public static void main(String[] args) {
        SpinLock spinLock = new SpinLock();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+"开始尝试获取自旋锁");
                spinLock.lock();
                System.out.println(Thread.currentThread().getName()+"获取到了自旋锁");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    spinLock.unlock();
                    System.out.println(Thread.currentThread().getName()+"释放了自旋锁");
                }
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
    }
}

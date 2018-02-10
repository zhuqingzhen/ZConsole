package com.zqz.test.threadstate;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 各种锁线程栈
 * @author zqz
 *
 */
public class ThreadStateTest {
	
	public Boolean lock = new Boolean(true);
	public Object lock2 = new Object();
	Lock rtlock = new ReentrantLock();
	Lock rtlock2 = new ReentrantLock();
	Condition  condition = rtlock2.newCondition();
	
	public static void main(String[] args) throws InterruptedException {
		ThreadStateTest twm = new ThreadStateTest();
		System.out.println(twm.lock.hashCode());
		System.out.println(twm.lock2.hashCode());
		LockSupportPark lsp =twm.new LockSupportPark(1);
		lsp.start();
		LockSupportPark2 lsp2 = twm.new LockSupportPark2(1);
		lsp2.start();
		
		Monitor_WaitSet mws = twm.new Monitor_WaitSet(1);
		mws.start();
		LockedSleeping ls = twm.new LockedSleeping(1);
		ls.start();
		Monitor_EntrySet wm =twm.new Monitor_EntrySet(1);
		wm.start();
		Monitor_EntrySet wm2 =twm.new Monitor_EntrySet(2);
		wm2.start();
		
		RTlockLocked rtl = twm.new RTlockLocked(1);
		rtl.start();
		RTlockWaitLocke rtwl = twm.new RTlockWaitLocke(1);
		rtwl.start();
		RTlockWaitLocke rtwl2 = twm.new RTlockWaitLocke(2);
		rtwl2.start();
		RTlockLockedConditionWait rlcw = twm.new RTlockLockedConditionWait(1);
		rlcw.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
class RTlockLockedConditionWait extends Thread{
		
	RTlockLockedConditionWait(int i){
			super("RTlock ConditionWait"+i);
		}
		
		public void run(){
			rtlock2.lock();
			System.out.println(Thread.currentThread().getName()+"--持rtLock有锁,然后await");
			try {
				condition.await();
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"+释放rtLock");
			rtlock2.unlock();
			
		}
	}
	
	class RTlockLocked extends Thread{
		
		RTlockLocked(int i){
			super("RTlock has Lock"+i);
		}
		
		public void run(){
			rtlock.lock();
			System.out.println(Thread.currentThread().getName()+"--持rtLock有锁休眠");
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"+释放rtLock");
			rtlock.unlock();
			
		}
	}
	
	class RTlockWaitLocke extends Thread{
		
		RTlockWaitLocke(int i){
			super("RTlock wait Lock"+i);
		}
		
		public void run(){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"--等待rtLock");
			rtlock.lock();
			System.out.println(Thread.currentThread().getName()+"--持rtLock有锁休眠");
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"+释放rtLock");
			rtlock.unlock();
			
		}
	}
	
	
	class Monitor_EntrySet extends Thread{
		
		Monitor_EntrySet(int i){
			super("EntrySet--waiting to lock---Waiting for monitor entry"+i);
		}
		
		public void run(){
			Monitor_EntrySet_run();
		}
		
		private void Monitor_EntrySet_run(){
			System.out.println(Thread.currentThread().getName()+"--休眠10秒钟");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"+开始同步");
			synchronized(lock){
				System.out.println(Thread.currentThread().getName()+"dowork");
			}
		}
	}
	
	class Monitor_WaitSet extends Thread{
		
		Monitor_WaitSet(int i){
			super("WaitSet--locked--waiting on--in Object.wait()"+i);
		}
		
		public void run(){
			Monitor_WaitSet_run();
		}
		
		private void Monitor_WaitSet_run(){
			System.out.println(Thread.currentThread().getName()+"+开始同步");
			synchronized(lock){
				try {
					System.out.println(Thread.currentThread().getName()+"+进入等待区");
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName()+"dowork");
			}
		}
	}
	
	
	
	
	class LockedSleeping extends Thread{
		int i =0;
		
		LockedSleeping(int i){
			super("locked--sleeping--wait on Condition"+i);
		}
		
		public void run(){
			LockedSleeping_run();
		}
		
		private void LockedSleeping_run(){
			System.out.println(Thread.currentThread().getName()+"--休眠5秒钟--"+i++);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"+开始同步--"+i++);
			synchronized(lock){
				System.out.println(Thread.currentThread().getName()+" do work--"+i++);
				System.out.println(Thread.currentThread().getName()+" to lock lock2--"+i++);
				synchronized(lock2){
					System.out.println(Thread.currentThread().getName()+" start sleep--"+i++);
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}
	
	class LockSupportPark extends Thread{
		LockSupportPark(int i){
			super("LockSupportPark--park"+i);
		}
		
		public void run(){
			parkMethod();
		}
		public void parkMethod(){
			park();
		}
		
		public void park(){
			System.out.println(Thread.currentThread().getName()+"开始park");
			LockSupport.park();
		}
	}
	
	
	class LockSupportPark2 extends Thread{
		LockSupportPark2(int i){
			super("LockSupportPark2--park"+i);
		}
		
		public void run(){
			parkMethod();
		}
		public void parkMethod(){
			park();
		}
		
		public void park(){
			System.out.println(Thread.currentThread().getName()+"开始park");
			LockSupport.park(Thread.currentThread());
		}
	}

}

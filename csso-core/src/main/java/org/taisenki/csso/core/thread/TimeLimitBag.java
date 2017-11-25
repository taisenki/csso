/** 
 * Project Name:csso-core 
 * File Name:TimeLimitBag.java 
 * Package Name:org.taisenki.csso.core.thread 
 * Date:2017年11月22日上午11:29:26 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.core.thread;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/** 
 * 具有时限的对象存储类 <br/> 
 *
 * @Date:    2017年11月22日 上午11:29:26 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public class TimeLimitBag<K,V> implements Closeable {
	
	private final AtomicBoolean down = new AtomicBoolean(true);

	private ScheduledThreadPoolExecutor cancelExecutor;
	private ConcurrentHashMap<K, ScheduledFuture<?>> tasks;
	
	private ScheduledThreadPoolExecutor guardPool;
	private ScheduledFuture<?> guardTask;
	
	private final ConcurrentHashMap<K, V> values = new ConcurrentHashMap<K, V>();
	
	public TimeLimitBag(){
		init();
	}
	
	protected void init(){
		this.cancelExecutor = createThreadPool("cancel-pool", 5, false);
		this.tasks = new ConcurrentHashMap<K, ScheduledFuture<?>>();
		
		this.guardPool = createThreadPool("timeLimit-guard-pool", 1, true);
		this.guardTask = this.guardPool.scheduleWithFixedDelay(getGuardTask(), 100l, 100l, TimeUnit.MILLISECONDS);
		
		this.down.set(false);
	}
	
	/** 
	 * put:放置对象并制定超时时间，时间单位为s. <br/> 
	 * {@link #close()}方法调用后禁止放入，直接返回
	 * 
	 * @author taisenki 
	 * @param id
	 * @param value
	 * @param expire 超时时间，s
	 * @since 
	 */  
	public void put(K id, V value, long expire) {
		if(id == null){
			return;
		}
		put(id, value, expire, TimeUnit.SECONDS);
	}
	
	/** 
	 * put:放置对象并制定超时时间. <br/> 
	 * {@link #close()}方法调用后禁止放入，直接返回
	 * 
	 * @author taisenki 
	 * @param id
	 * @param value
	 * @param expire
	 * @param unit 
	 * @since 
	 */  
	public void put(K id, V value, long expire, TimeUnit unit) {
		if(down.get()) return;
		if(id == null){
			return;
		}
		values.put(id, value);
		addRemoveTask(id, expire, unit);
	}
	
	public V get(K id){
		if(id == null){
			return null;
		}
		return values.get(id);
	}
	
	public boolean containsKey(K id){
		if(id == null){
			return false;
		}
		return values.containsKey(id);
	}
	
	public void remove(K id){
		if(containsKey(id)){
			ScheduledFuture<?> future = this.tasks.get(id);
			// 防止并发时已经被超时移除的情况
			if(future != null){
				future.cancel(true);
				remove0(id);
			}
		}
	}
	
	protected void remove0(K id){
		this.values.remove(id);
		this.tasks.remove(id);
	}
	
	protected void addRemoveTask(K id, long delay, TimeUnit unit){
		ScheduledFuture<?> future = this.cancelExecutor.schedule(new RemoveTask(id), delay, unit);
		this.tasks.put(id, future);
	}

	@Override
	public void close() throws IOException {
		if(this.down.compareAndSet(false, true)){	
			//优先情况数据
			this.values.clear();
			//取消超时任务
			this.cancelExecutor.shutdownNow();
			this.tasks.clear();			
			//取消守护线程
			this.guardTask.cancel(true);
			this.guardPool.shutdownNow();
		};		
	}
	
	/** 
	 * getGuardTask:循环执行的守护任务，默认是空跑，可进行自定义实现. <br/> 
	 * 
	 * @author taisenki 
	 * @return 
	 * @since 
	 */  
	protected Runnable getGuardTask(){
		return new UndoTask();
	}
	
	private ScheduledThreadPoolExecutor createThreadPool(String poolName, int num, boolean isDaemon){
		ThreadFactory b = new ThreadFactoryBuilder().setDaemon(isDaemon).setNameFormat(poolName+"-%d").build();
		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(num, 
				b,	new ThreadPoolExecutor.DiscardPolicy());
		executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
//		executor.setRemoveOnCancelPolicy(true);
		return executor;
	}
	
	private class RemoveTask implements Runnable {

		private K id;
		
		RemoveTask(K id) { this.id = id; }
		
		@Override
		public void run() {
			remove0(id);
		}
		
	}
	
	private class UndoTask implements Runnable{
		
		@Override
		public void run() {
		}
		
	}
}
  
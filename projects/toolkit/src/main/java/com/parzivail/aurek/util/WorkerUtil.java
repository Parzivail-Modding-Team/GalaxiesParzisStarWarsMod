package com.parzivail.aurek.util;

import com.google.common.util.concurrent.MoreExecutors;
import com.parzivail.aurek.ToolkitClient;
import net.minecraft.Bootstrap;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerUtil
{
	private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);

	public static ExecutorService createWorker(String name, int maxThreads)
	{
		int numThreads = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, maxThreads);
		ExecutorService executorService;
		if (numThreads <= 0)
			executorService = MoreExecutors.newDirectExecutorService();
		else
		{
			executorService = new ForkJoinPool(numThreads, (forkJoinPool) -> {
				ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool)
				{
					@Override
					protected void onTermination(Throwable throwable)
					{
						if (throwable != null)
						{
							ToolkitClient.LOG.warn("{} died", this.getName(), throwable);
						}
						else
						{
							ToolkitClient.LOG.debug("{} shutdown", this.getName());
						}

						super.onTermination(throwable);
					}
				};
				forkJoinWorkerThread.setName("Aurek-" + name + "-" + NEXT_WORKER_ID.getAndIncrement());
				return forkJoinWorkerThread;
			}, WorkerUtil::uncaughtExceptionHandler, true);
		}

		return executorService;
	}

	private static void uncaughtExceptionHandler(Thread thread, Throwable t)
	{
		Util.throwOrPause(t);
		if (t instanceof CompletionException)
		{
			t = t.getCause();
		}

		if (t instanceof CrashException)
		{
			Bootstrap.println(((CrashException)t).getReport().asString());
			System.exit(-1);
		}

		ToolkitClient.LOG.error(String.format(Locale.ROOT, "Caught exception in thread %s", thread), t);
	}

	public static ExecutorService createThreadPool(int nThreads, String name)
	{
		return Executors.newFixedThreadPool(
				nThreads,
				(runnable) ->
				{
					Thread thread = new Thread(runnable);
					thread.setName(name);
					thread.setUncaughtExceptionHandler(WorkerUtil::uncaughtExceptionHandler);
					return thread;
				}
		);
	}
}

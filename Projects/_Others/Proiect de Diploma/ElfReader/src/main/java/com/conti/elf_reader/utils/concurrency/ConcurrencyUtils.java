package com.conti.elf_reader.utils.concurrency;

import com.utils.log.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrencyUtils {

    public static void executeMultithreadedTask(
            int numberOfThreads, List<Callable<Void>> callableList) throws Exception {

        Logger.printLine("(number of threads: " + numberOfThreads + ")");
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final List<Future<Void>> futures = executorService.invokeAll(callableList);
        for (Future<Void> future : futures) {
            future.get();
        }
        executorService.shutdown();
    }
}

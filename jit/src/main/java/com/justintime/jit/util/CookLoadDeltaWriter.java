package com.justintime.jit.util;

import com.justintime.jit.dto.CookLoadDeltaDTO;
import com.justintime.jit.entity.CookLoadDelta;
import com.justintime.jit.repository.CookLoadDeltaRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.concurrent.*;

@Component
public class CookLoadDeltaWriter implements InitializingBean, DisposableBean {
    private final BlockingQueue<CookLoadDeltaDTO> queue = new ArrayBlockingQueue<>(5000);
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> new Thread(r, "cook-delta-writer"));
    private final CookLoadDeltaRepository cookLoadDeltaRepository;
    private volatile boolean running = true;

    public CookLoadDeltaWriter(CookLoadDeltaRepository cookLoadDeltaRepository) {
        this.cookLoadDeltaRepository = cookLoadDeltaRepository;
    }

    public void enqueue(CookLoadDeltaDTO delta) {
        boolean accepted = queue.offer(delta);
        if (!accepted) {
            cookLoadDeltaRepository.save(new CookLoadDelta(delta.cookId(), delta.deltaMinutes(), delta.orderItemId(), Timestamp.from(delta.createdAt()), delta.processed()));
        }
    }

    @Override
    public void destroy() {
        running = false;
        worker.shutdown();
        try { worker.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
    }

    @Override
    public void afterPropertiesSet() {
        worker.submit(this::processQueue);
    }

    private void processQueue() {
        while (running || !queue.isEmpty()) {
            try {
                CookLoadDeltaDTO delta = queue.poll(1, TimeUnit.SECONDS);
                if (delta != null) {
                    saveCookLoadDelta(delta);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // preserve interrupt status
                break; // exit loop on interrupt
            } catch (Exception ex) {
                System.out.println("Error processing cook load delta :: " + ex);
            }
        }
    }

    private void saveCookLoadDelta(CookLoadDeltaDTO delta) {
        CookLoadDelta entity = new CookLoadDelta(
                delta.cookId(),
                delta.deltaMinutes(),
                delta.orderItemId(),
                Timestamp.from(delta.createdAt()),
                true
        );
        cookLoadDeltaRepository.save(entity);
    }
}

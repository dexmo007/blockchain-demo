package com.dexmohq.blockchain.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAuthority('USER')")
public class BlockchainController {

    private static final Logger log = LoggerFactory.getLogger(BlockchainController.class);

    @GetMapping(path = "mine")
    public Future<Nonce> mineGet(@RequestParam("data") String data) {
        return CompletableFuture.supplyAsync(() -> new Nonce(mineSeq(data)));
    }

    @PostMapping(path = "mine")
    public Future<Nonce> minePost(@RequestBody Data data) {
        return CompletableFuture.supplyAsync(() -> new Nonce(mineParallel(data.getData())));
    }

    public long mineSeq(String data) {
        int nonce = 0;// treated as unsigned
        final MessageDigest md = newSha256Digest();
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        final byte[] input = new byte[bytes.length + Integer.BYTES];
        System.arraycopy(bytes, 0, input, 4, bytes.length);
        do {
            input[0] = (byte) (nonce & 0xff);
            input[1] = (byte) ((nonce >>> 4) & 0xff);
            input[2] = (byte) ((nonce >>> 8) & 0xff);
            input[3] = (byte) ((nonce >>> 12) & 0xff);
            final byte[] hashBytes = md.digest(input);
            if (hashBytes[0] == 0 && hashBytes[1] == 0) {
                return Integer.toUnsignedLong(nonce);//todo check if this does the expected
            }
            md.reset();
            nonce++;
        } while (nonce != 0);//while nonce reaches zero again due to numeric overflow -> we tested all 4-byte ints
        throw new IllegalStateException("No nonce found");
    }

    public long mineParallel(String data) {
        return mineParallel(data, Runtime.getRuntime().availableProcessors());
    }

    public long mineParallel(String data, int parallelism) {
        final ExecutorService es = Executors.newFixedThreadPool(parallelism);
        final ArrayList<Callable<Integer>> tasks = new ArrayList<>();
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < parallelism; i++) {
            final int starting = i;
            tasks.add(() -> {
                final byte[] input = new byte[Integer.BYTES + bytes.length];//thread local data array that can be prefixed by the 4 nonce bytes
                System.arraycopy(bytes, 0, input, Integer.BYTES, bytes.length);
                final MessageDigest md = newSha256Digest();// thread local message digest
                int nonce = starting;
                do {
                    input[0] = (byte) (nonce & 0xff);
                    input[1] = (byte) ((nonce >>> 8) & 0xff);
                    input[2] = (byte) ((nonce >>> 16) & 0xff);
                    input[3] = (byte) ((nonce >>> 24) & 0xff);
                    final byte[] hashBytes = md.digest(input);
                    if (hashBytes[0] == 0 && hashBytes[1] == 0) {
                        return nonce;
                    }
                    md.reset();
                    nonce += parallelism;
                } while (nonce != starting && !es.isShutdown());

                throw new IllegalStateException("No nonce found");
            });
        }
        try {
            final int nonce = es.invokeAny(tasks);
            es.shutdownNow();
            return Integer.toUnsignedLong(nonce);//todo see other
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            es.shutdownNow();
        }
    }

    private MessageDigest newSha256Digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError(e);
        }
    }

    static class Data {
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    static class Nonce {
        private final long nonce;

        public Nonce(long nonce) {
            this.nonce = nonce;
        }

        public long getNonce() {
            return nonce;
        }

    }

}

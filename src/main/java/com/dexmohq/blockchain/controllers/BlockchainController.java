package com.dexmohq.blockchain.controllers;

import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAuthority('USER')")
public class BlockchainController {

    private static final Logger log = LoggerFactory.getLogger(BlockchainController.class);

    @GetMapping(path = "mine")
    public Future<Nonce> mineGet(@RequestParam("data") String data) {
        return CompletableFuture.supplyAsync(() -> new Nonce(doMining(data)));
    }

    @PostMapping(path = "mine")
    public Future<Nonce> minePost(@RequestBody Data data) {
        return CompletableFuture.supplyAsync(() -> new Nonce(doMining(data.getData())));
    }

    private long doMining(String data) {
        for (long i = -1; i < Long.MAX_VALUE; i++) {
            final long nonce = i + 1;
            final String hash = Hashing.sha256().hashString(Long.toString(nonce) + data, StandardCharsets.UTF_8).toString();
            if (hash.startsWith("0000")) {
                return nonce;
            }
        }
        throw new IllegalStateException("No nonce found");
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

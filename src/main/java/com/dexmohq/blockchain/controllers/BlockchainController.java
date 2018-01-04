package com.dexmohq.blockchain.controllers;

import com.google.common.hash.Hashing;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class BlockchainController {

    @GetMapping(path = "mine")
    @PreAuthorize("hasAuthority('USER')")
    public Future<Long> mine(@RequestParam("data") String data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);//todo rm
            } catch (InterruptedException e) {
                //ignore
            }

            for (long i = -1; i < Long.MAX_VALUE ; i++) {
                final long nonce = i + 1;
                final String hash = Hashing.sha256().hashString(Long.toString(nonce) + data, StandardCharsets.UTF_8).toString();
                if (hash.startsWith("0000")) {
                    return nonce;
                }
            }
            throw new IllegalStateException("No nonce found");
        });
    }

    @PostMapping(path = "mine")
    public BlockDto mineBlock(@RequestBody BlockDto block) {
        long nonce = 0;
        String hash = "";
        while (!hash.startsWith("0000")) {
            hash = Hashing.sha256().hashString(nonce + block.data, StandardCharsets.UTF_8).toString();
            nonce++;
        }
        block.nonce = nonce;
        return block;
    }

    public static class BlockDto {
        private long nonce;
        private String data;

        public long getNonce() {
            return nonce;
        }

        public void setNonce(long nonce) {
            this.nonce = nonce;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "BlockDto{" +
                    "nonce=" + nonce +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

}

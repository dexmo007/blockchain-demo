package com.dexmohq.blockchain.controllers;

import com.dexmohq.blockchain.model.Blockchain;
import com.google.common.hash.Hashing;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class BlockchainController {

    @GetMapping(path = "mine")
    public long mine(@RequestParam("data") String data) {
        long nonce = 0;
        String hash = "";
        while (!hash.startsWith("0000")) {
            hash = Hashing.sha256().hashString(nonce + data, StandardCharsets.UTF_8).toString();
            nonce++;
        }
        return nonce;
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

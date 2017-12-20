package com.dexmohq.blockchain.model;

import com.google.common.hash.Hashing;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

public class Blockchain implements Iterable<Blockchain.Block> {

    private Block last;

    public Blockchain(byte[] firstBlockData) {
        last = new Block();
        last.index = 0;
        last.data = firstBlockData;
        last.mine();
    }

    public Blockchain addBlock(byte[] blockData) {
        Block current = new Block();
        current.index = last.index + 1;
        current.data = blockData;
        current.mine();

        current.previous = last;
        last = current;
        return this;
    }

    @Override
    public Iterator<Block> iterator() {
        return new Iterator<Block>() {

            private Block current = last;

            @Override
            public boolean hasNext() {
                return current.previous != null;
            }

            @Override
            public Block next() {
                Block next = this.current;
                this.current = next.previous;
                return next;
            }
        };
    }

    public Stream<Block> stream() {
        return Stream.iterate(last, block -> block.previous);
    }


    public boolean isValid() {
        return stream().allMatch(Block::isValid);
    }


    public class Block {
        private int index;

        private long nonce;

        private byte[] data;

        private Block previous;

        private String hash;

        private boolean isValid() {
            return hash.startsWith("0000");
        }

        private void mine() {
            long nonce = 0;
            String hash = "";
            while (!hash.startsWith("0000")) {
                final ByteBuffer buffer = ByteBuffer.allocate(4 + data.length).putLong(nonce).put(data);
                hash = Hashing.sha256().hashBytes(buffer).toString();
                nonce++;
            }
            this.nonce = nonce;
            this.hash = hash;

        }
    }

}

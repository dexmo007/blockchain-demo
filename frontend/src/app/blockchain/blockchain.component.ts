import {Component, Input, OnInit} from '@angular/core';
import index from "@angular/cli/lib/cli";

export interface Block {
  hash?: string;
  prev: string;
}

@Component({
  selector: 'app-blockchain',
  templateUrl: './blockchain.component.html',
  styleUrls: ['./blockchain.component.css']
})
export class BlockchainComponent implements OnInit {

  blocks: Block[] = [{prev: ''}];

  constructor() {
  }

  ngOnInit() {
  }

  blockChanged($event: {index: number, hash: string}) {
    if ($event.index == this.blocks.length - 1) {
      //do nothing for last block in chain
      return;
    }
    this.blocks[$event.index + 1].prev = $event.hash;
  }

  addBlock() {
    this.blocks.push({
      prev: this.blocks[this.blocks.length - 1].hash
    })
  }

}

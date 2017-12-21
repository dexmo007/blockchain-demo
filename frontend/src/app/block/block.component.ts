import {Component, Input, OnInit} from '@angular/core';
import * as shajs from 'sha.js';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-block',
  templateUrl: './block.component.html',
  styleUrls: ['./block.component.css']
})
export class BlockComponent implements OnInit {
  @Input() index: number;
  nonce: number = 0;
  data: string = "";
  hash: string = this.getHash();
  isMining: boolean = false;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
  }

  mine() {
    this.isMining = true;
    this.http.get('/api/mine?data=' + this.data)
      .subscribe(res => {
          this.nonce = Number(res);
          this.rehash();
          this.isMining = false;
        }
      )
  }

  rehash() {
    this.hash = this.getHash();
  }

  private getHash(): string {
    return shajs('sha256').update(`${this.nonce}${this.data}`).digest('hex');
  }

  isValid(): boolean {
    return this.hash.startsWith('0000');
  }
}

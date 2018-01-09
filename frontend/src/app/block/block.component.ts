import {
  ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output,
  SimpleChanges
} from '@angular/core';
import * as shajs from 'sha.js';
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-block',
  templateUrl: './block.component.html',
  styleUrls: ['./block.component.css']
})
export class BlockComponent implements OnInit {
  ngOnInit(): void {
  }

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, private auth: AuthService) {
  }

  @Input() index: number;
  nonce: number = 0;
  data: string = "";

  private _previousHash: string;
  get previousHash(): string {
    return this._previousHash;
  }

  @Input()
  set previousHash(value: string) {
    if (value === undefined) {
      return;
    }
    this._previousHash = value;
    // async rehashing is required to assure changing the value after verification loop
    setTimeout(() => {
      this.rehash();
    });
  }

  private _hash: string = this.getHash();
  isMining: boolean = false;

  @Input() get hash(): string {
    return this._hash;
  }

  @Output() hashChange: EventEmitter<{hash: string, index: number}> = new EventEmitter();

  set hash(value: string) {
    if (value === undefined) {
      return;
    }
    this._hash = value;
    this.hashChange.emit({index: this.index, hash: this._hash});
  }

  mine() {
    this.isMining = true;
    this.http.get('/api/mine?data=' + this.data + this._previousHash)
      .subscribe((res: {nonce: number}) => {
          this.nonce = res.nonce;
          this.rehash();
          this.isMining = false;
        }, error => {
          console.log(error);
          this.isMining = false;
        }
      )
  }

  rehash() {
    this.hash = this.getHash();
  }

  private getHash(): string {
    return shajs('sha256').update(`${this.nonce}${this.data}${this._previousHash}`).digest('hex');
  }

  isValid(): boolean {
    return this._hash.startsWith('0000');
  }
}

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule} from "@ng-bootstrap/ng-bootstrap";

import { AppComponent } from './app.component';
import { BlockComponent } from './block/block.component';
import { DigitsOnlyDirective } from './util/digits-only.directive';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    BlockComponent,
    DigitsOnlyDirective
  ],
  imports: [
    BrowserModule, NgbModule.forRoot(), HttpClientModule, FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[digitsOnly]'
})
export class DigitsOnlyDirective {

  constructor(private element: ElementRef) { }

  private regex: RegExp = new RegExp(/^-?[0-9]+(\.[0-9]*)?$/g);

  private specialKeys: Array<string> = [ 'Backspace', 'Tab', 'End', 'Home', '-' ];

  @HostListener('keydown', [ '$event' ])
  onKeyDown(event: KeyboardEvent) {
    // Allow Backspace, tab, end, and home keys
    if (this.specialKeys.indexOf(event.key) !== -1) {
      return;
    }
    let current: string = this.element.nativeElement.value;
    let next: string = current.concat(event.key);
    if (next && !String(next).match(this.regex)) {
      event.preventDefault();
    }
  }

}

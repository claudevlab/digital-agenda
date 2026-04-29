import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-modal.component.html',
  styleUrl: './confirm-modal.component.scss'
})
export class ConfirmModalComponent {

  @Input() title: string = 'Conferma';
  @Input() message: string = 'Sei sicuro?';
  @Input() confirmLabel: string = 'Conferma';
  @Input() cancelLabel: string = 'Annulla';
  @Input() confirmClass: string = 'btn-danger'; // colore pulsante conferma

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  onConfirm(): void {
    this.confirmed.emit();
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
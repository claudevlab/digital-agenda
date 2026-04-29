import { TestBed } from '@angular/core/testing';

import { ScheduleExceptionService } from './schedule-exception.service';

describe('ScheduleExceptionService', () => {
  let service: ScheduleExceptionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduleExceptionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

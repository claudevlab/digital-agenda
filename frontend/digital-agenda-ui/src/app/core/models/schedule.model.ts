// gestione dell'enum dal backend
export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface Schedule {
    id : number;
    dayOfWeek : DayOfWeek;
    startTime : string // lo facciamo arrivare come stringa 
    endTime : string;
    professionalId : number;

}

export interface ScheduleRequest {
    dayOfWeek: DayOfWeek;
    startTime : string 
    endTime : string;
    description?: string;
}

export interface AvailableSlotDTO {
    startTime : string;
    endTime: string;
    available : boolean;
}
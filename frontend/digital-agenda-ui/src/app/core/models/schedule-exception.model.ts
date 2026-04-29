
// angular --> backend
export interface  ScheduleExceptionRequest {
    date : string;
    reason? : string;
}

// backend --> angular
export interface ScheduleExceptionResponse {
    id : number;
    date : string;
    reason? : string;
}
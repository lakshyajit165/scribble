import { INoteResponseObject } from "./INoteResponseObject";

export interface ISearchNotesResponse {
    content: INoteResponseObject[];
    message: string;
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    last: boolean;
}
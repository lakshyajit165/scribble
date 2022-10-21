import { IScribbleResponseObject } from "./IScribbleResponseObject";

export interface ISearchNotesResponse {
    content: IScribbleResponseObject[];
    message: string;
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    last: boolean;
}
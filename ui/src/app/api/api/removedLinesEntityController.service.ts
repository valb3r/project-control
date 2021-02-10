/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent, HttpParameterCodec }       from '@angular/common/http';
import { CustomHttpParameterCodec }                          from '../encoder';
import { Observable }                                        from 'rxjs';

import { CollectionModelEntityModelRemovedLines } from '../model/models';
import { EntityModelRemovedLines } from '../model/models';
import { RemovedLines } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class RemovedLinesEntityControllerService {

    protected basePath = 'http://localhost:8080';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();
    public encoder: HttpParameterCodec;

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (configuration) {
            this.configuration = configuration;
        }
        if (typeof this.configuration.basePath !== 'string') {
            if (typeof basePath !== 'string') {
                basePath = this.basePath;
            }
            this.configuration.basePath = basePath;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }


    private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
        if (typeof value === "object" && value instanceof Date === false) {
            httpParams = this.addToHttpParamsRecursive(httpParams, value);
        } else {
            httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
        }
        return httpParams;
    }

    private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
        if (value == null) {
            return httpParams;
        }

        if (typeof value === "object") {
            if (Array.isArray(value)) {
                (value as any[]).forEach( elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
            } else if (value instanceof Date) {
                if (key != null) {
                    httpParams = httpParams.append(key,
                        (value as Date).toISOString().substr(0, 10));
                } else {
                   throw Error("key may not be null if value is Date");
                }
            } else {
                Object.keys(value).forEach( k => httpParams = this.addToHttpParamsRecursive(
                    httpParams, value[k], key != null ? `${key}.${k}` : k));
            }
        } else if (key != null) {
            httpParams = httpParams.append(key, value);
        } else {
            throw Error("key may not be null if value is not object or array");
        }
        return httpParams;
    }

    /**
     * delete-removedlines
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deleteItemResourceRemovedlinesDelete(id: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<any>;
    public deleteItemResourceRemovedlinesDelete(id: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<HttpResponse<any>>;
    public deleteItemResourceRemovedlinesDelete(id: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<HttpEvent<any>>;
    public deleteItemResourceRemovedlinesDelete(id: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: undefined}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deleteItemResourceRemovedlinesDelete.');
        }

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.delete<any>(`${this.configuration.basePath}/v1/resources/removedLineses/${encodeURIComponent(String(id))}`,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * get-removedlines
     * @param page Zero-based page index (0..N)
     * @param size The size of the page to be returned
     * @param sort Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getCollectionResourceRemovedlinesGet1(page?: number, size?: number, sort?: Array<string>, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json' | 'application/x-spring-data-compact+json' | 'text/uri-list'}): Observable<CollectionModelEntityModelRemovedLines>;
    public getCollectionResourceRemovedlinesGet1(page?: number, size?: number, sort?: Array<string>, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json' | 'application/x-spring-data-compact+json' | 'text/uri-list'}): Observable<HttpResponse<CollectionModelEntityModelRemovedLines>>;
    public getCollectionResourceRemovedlinesGet1(page?: number, size?: number, sort?: Array<string>, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json' | 'application/x-spring-data-compact+json' | 'text/uri-list'}): Observable<HttpEvent<CollectionModelEntityModelRemovedLines>>;
    public getCollectionResourceRemovedlinesGet1(page?: number, size?: number, sort?: Array<string>, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json' | 'application/x-spring-data-compact+json' | 'text/uri-list'}): Observable<any> {

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (page !== undefined && page !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>page, 'page');
        }
        if (size !== undefined && size !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>size, 'size');
        }
        if (sort) {
            sort.forEach((element) => {
                queryParameters = this.addToHttpParams(queryParameters,
                  <any>element, 'sort');
            })
        }

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/hal+json',
                'application/x-spring-data-compact+json',
                'text/uri-list'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.get<CollectionModelEntityModelRemovedLines>(`${this.configuration.basePath}/v1/resources/removedLineses`,
            {
                params: queryParameters,
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * get-removedlines
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getItemResourceRemovedlinesGet(id: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelRemovedLines>;
    public getItemResourceRemovedlinesGet(id: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelRemovedLines>>;
    public getItemResourceRemovedlinesGet(id: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelRemovedLines>>;
    public getItemResourceRemovedlinesGet(id: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling getItemResourceRemovedlinesGet.');
        }

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/hal+json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.get<EntityModelRemovedLines>(`${this.configuration.basePath}/v1/resources/removedLineses/${encodeURIComponent(String(id))}`,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * patch-removedlines
     * @param id 
     * @param removedLines 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public patchItemResourceRemovedlinesPatch(id: string, removedLines?: RemovedLines, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelRemovedLines>;
    public patchItemResourceRemovedlinesPatch(id: string, removedLines?: RemovedLines, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelRemovedLines>>;
    public patchItemResourceRemovedlinesPatch(id: string, removedLines?: RemovedLines, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelRemovedLines>>;
    public patchItemResourceRemovedlinesPatch(id: string, removedLines?: RemovedLines, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling patchItemResourceRemovedlinesPatch.');
        }

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/hal+json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.patch<EntityModelRemovedLines>(`${this.configuration.basePath}/v1/resources/removedLineses/${encodeURIComponent(String(id))}`,
            removedLines,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * create-removedlines
     * @param removedLines 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public postCollectionResourceRemovedlinesPost(removedLines?: RemovedLines, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelRemovedLines>;
    public postCollectionResourceRemovedlinesPost(removedLines?: RemovedLines, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelRemovedLines>>;
    public postCollectionResourceRemovedlinesPost(removedLines?: RemovedLines, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelRemovedLines>>;
    public postCollectionResourceRemovedlinesPost(removedLines?: RemovedLines, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/hal+json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<EntityModelRemovedLines>(`${this.configuration.basePath}/v1/resources/removedLineses`,
            removedLines,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * update-removedlines
     * @param id 
     * @param removedLines 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public putItemResourceRemovedlinesPut(id: string, removedLines?: RemovedLines, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelRemovedLines>;
    public putItemResourceRemovedlinesPut(id: string, removedLines?: RemovedLines, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelRemovedLines>>;
    public putItemResourceRemovedlinesPut(id: string, removedLines?: RemovedLines, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelRemovedLines>>;
    public putItemResourceRemovedlinesPut(id: string, removedLines?: RemovedLines, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling putItemResourceRemovedlinesPut.');
        }

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/hal+json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.put<EntityModelRemovedLines>(`${this.configuration.basePath}/v1/resources/removedLineses/${encodeURIComponent(String(id))}`,
            removedLines,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}

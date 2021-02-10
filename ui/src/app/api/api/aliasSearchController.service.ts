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

import { CollectionModelEntityModelAlias } from '../model/models';
import { EntityModelAlias } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class AliasSearchControllerService {

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
     * @param name 
     * @param repoId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public findByNameAndRepoIdSl(name?: string, repoId?: number, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelAlias>;
    public findByNameAndRepoIdSl(name?: string, repoId?: number, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelAlias>>;
    public findByNameAndRepoIdSl(name?: string, repoId?: number, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelAlias>>;
    public findByNameAndRepoIdSl(name?: string, repoId?: number, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (name !== undefined && name !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>name, 'name');
        }
        if (repoId !== undefined && repoId !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>repoId, 'repoId');
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

        return this.httpClient.get<EntityModelAlias>(`${this.configuration.basePath}/v1/resources/aliases/search/findByNameAndRepoId`,
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
     * @param repoId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public findByRepoIdL(repoId?: number, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<CollectionModelEntityModelAlias>;
    public findByRepoIdL(repoId?: number, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<CollectionModelEntityModelAlias>>;
    public findByRepoIdL(repoId?: number, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<CollectionModelEntityModelAlias>>;
    public findByRepoIdL(repoId?: number, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (repoId !== undefined && repoId !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>repoId, 'repoId');
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

        return this.httpClient.get<CollectionModelEntityModelAlias>(`${this.configuration.basePath}/v1/resources/aliases/search/findByRepoId`,
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
     * @param aliasId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public findRelatedToL(aliasId?: number, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<CollectionModelEntityModelAlias>;
    public findRelatedToL(aliasId?: number, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<CollectionModelEntityModelAlias>>;
    public findRelatedToL(aliasId?: number, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<CollectionModelEntityModelAlias>>;
    public findRelatedToL(aliasId?: number, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (aliasId !== undefined && aliasId !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>aliasId, 'aliasId');
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

        return this.httpClient.get<CollectionModelEntityModelAlias>(`${this.configuration.basePath}/v1/resources/aliases/search/findRelatedTo`,
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

}

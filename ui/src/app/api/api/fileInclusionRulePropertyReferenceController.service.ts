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

import { CollectionModelObject } from '../model/models';
import { EntityModelFileInclusionRule } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class FileInclusionRulePropertyReferenceControllerService {

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
     * patch-fileinclusionrule
     * @param id 
     * @param collectionModelObject 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public createPropertyReferenceFileinclusionrulePatch(id: string, collectionModelObject?: CollectionModelObject, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelFileInclusionRule>;
    public createPropertyReferenceFileinclusionrulePatch(id: string, collectionModelObject?: CollectionModelObject, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelFileInclusionRule>>;
    public createPropertyReferenceFileinclusionrulePatch(id: string, collectionModelObject?: CollectionModelObject, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelFileInclusionRule>>;
    public createPropertyReferenceFileinclusionrulePatch(id: string, collectionModelObject?: CollectionModelObject, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling createPropertyReferenceFileinclusionrulePatch.');
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
            'application/json',
            'application/x-spring-data-compact+json',
            'text/uri-list'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.patch<EntityModelFileInclusionRule>(`${this.configuration.basePath}/v1/resources/fileInclusionRules/${encodeURIComponent(String(id))}/repo`,
            collectionModelObject,
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
     * update-fileinclusionrule
     * @param id 
     * @param collectionModelObject 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public createPropertyReferenceFileinclusionrulePut(id: string, collectionModelObject?: CollectionModelObject, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelFileInclusionRule>;
    public createPropertyReferenceFileinclusionrulePut(id: string, collectionModelObject?: CollectionModelObject, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelFileInclusionRule>>;
    public createPropertyReferenceFileinclusionrulePut(id: string, collectionModelObject?: CollectionModelObject, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelFileInclusionRule>>;
    public createPropertyReferenceFileinclusionrulePut(id: string, collectionModelObject?: CollectionModelObject, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling createPropertyReferenceFileinclusionrulePut.');
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
            'application/json',
            'application/x-spring-data-compact+json',
            'text/uri-list'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.put<EntityModelFileInclusionRule>(`${this.configuration.basePath}/v1/resources/fileInclusionRules/${encodeURIComponent(String(id))}/repo`,
            collectionModelObject,
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
     * delete-fileinclusionrule
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deletePropertyReferenceFileinclusionruleDelete(id: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<any>;
    public deletePropertyReferenceFileinclusionruleDelete(id: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<HttpResponse<any>>;
    public deletePropertyReferenceFileinclusionruleDelete(id: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<HttpEvent<any>>;
    public deletePropertyReferenceFileinclusionruleDelete(id: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: undefined}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deletePropertyReferenceFileinclusionruleDelete.');
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

        return this.httpClient.delete<any>(`${this.configuration.basePath}/v1/resources/fileInclusionRules/${encodeURIComponent(String(id))}/repo`,
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
     * delete-fileinclusionrule
     * @param id 
     * @param propertyId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deletePropertyReferenceIdFileinclusionruleDelete(id: string, propertyId: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<any>;
    public deletePropertyReferenceIdFileinclusionruleDelete(id: string, propertyId: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<HttpResponse<any>>;
    public deletePropertyReferenceIdFileinclusionruleDelete(id: string, propertyId: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: undefined}): Observable<HttpEvent<any>>;
    public deletePropertyReferenceIdFileinclusionruleDelete(id: string, propertyId: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: undefined}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deletePropertyReferenceIdFileinclusionruleDelete.');
        }
        if (propertyId === null || propertyId === undefined) {
            throw new Error('Required parameter propertyId was null or undefined when calling deletePropertyReferenceIdFileinclusionruleDelete.');
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

        return this.httpClient.delete<any>(`${this.configuration.basePath}/v1/resources/fileInclusionRules/${encodeURIComponent(String(id))}/repo/${encodeURIComponent(String(propertyId))}`,
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
     * get-fileinclusionrule
     * @param id 
     * @param propertyId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public followPropertyReferenceFileinclusionruleGet(id: string, propertyId: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<EntityModelFileInclusionRule>;
    public followPropertyReferenceFileinclusionruleGet(id: string, propertyId: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpResponse<EntityModelFileInclusionRule>>;
    public followPropertyReferenceFileinclusionruleGet(id: string, propertyId: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<HttpEvent<EntityModelFileInclusionRule>>;
    public followPropertyReferenceFileinclusionruleGet(id: string, propertyId: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling followPropertyReferenceFileinclusionruleGet.');
        }
        if (propertyId === null || propertyId === undefined) {
            throw new Error('Required parameter propertyId was null or undefined when calling followPropertyReferenceFileinclusionruleGet.');
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

        return this.httpClient.get<EntityModelFileInclusionRule>(`${this.configuration.basePath}/v1/resources/fileInclusionRules/${encodeURIComponent(String(id))}/repo/${encodeURIComponent(String(propertyId))}`,
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
     * get-fileinclusionrule
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public followPropertyReferenceFileinclusionruleGet1(id: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json' | 'text/uri-list'}): Observable<EntityModelFileInclusionRule>;
    public followPropertyReferenceFileinclusionruleGet1(id: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json' | 'text/uri-list'}): Observable<HttpResponse<EntityModelFileInclusionRule>>;
    public followPropertyReferenceFileinclusionruleGet1(id: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/hal+json' | 'text/uri-list'}): Observable<HttpEvent<EntityModelFileInclusionRule>>;
    public followPropertyReferenceFileinclusionruleGet1(id: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/hal+json' | 'text/uri-list'}): Observable<any> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling followPropertyReferenceFileinclusionruleGet1.');
        }

        let headers = this.defaultHeaders;

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/hal+json',
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

        return this.httpClient.get<EntityModelFileInclusionRule>(`${this.configuration.basePath}/v1/resources/fileInclusionRules/${encodeURIComponent(String(id))}/repo`,
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

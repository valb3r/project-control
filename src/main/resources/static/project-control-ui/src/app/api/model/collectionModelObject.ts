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
import { Link } from './link';
import { CollectionModelObjectEmbedded } from './collectionModelObjectEmbedded';


export interface CollectionModelObject { 
    _embedded?: CollectionModelObjectEmbedded;
    _links?: { [key: string]: Link; };
}


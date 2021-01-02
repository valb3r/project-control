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


export interface EntityModelGitRepo { 
    id?: number;
    name?: string;
    branchToAnalyze?: string;
    url?: string;
    uuid?: string;
    needsAuthentication?: boolean;
    lastAnalyzedCommit?: string;
    analysisState?: EntityModelGitRepo.AnalysisStateEnum;
    lastGoodState?: EntityModelGitRepo.LastGoodStateEnum;
    commitsProcessed?: number;
    errorMessage?: string;
    _links?: { [key: string]: Link; };
}
export namespace EntityModelGitRepo {
    export type AnalysisStateEnum = 'NONE' | 'STARTED' | 'CLONING' | 'CLONED' | 'CHURN_COUNTING' | 'CHURN_COUNTED' | 'LOC_OWNERSHIP_COUNTING' | 'LOC_OWNERSHIP_COUNTED' | 'REFACTOR_COUNTING' | 'REFACTOR_COUNTED' | 'FINISHED' | 'FAILED';
    export const AnalysisStateEnum = {
        None: 'NONE' as AnalysisStateEnum,
        Started: 'STARTED' as AnalysisStateEnum,
        Cloning: 'CLONING' as AnalysisStateEnum,
        Cloned: 'CLONED' as AnalysisStateEnum,
        ChurnCounting: 'CHURN_COUNTING' as AnalysisStateEnum,
        ChurnCounted: 'CHURN_COUNTED' as AnalysisStateEnum,
        LocOwnershipCounting: 'LOC_OWNERSHIP_COUNTING' as AnalysisStateEnum,
        LocOwnershipCounted: 'LOC_OWNERSHIP_COUNTED' as AnalysisStateEnum,
        RefactorCounting: 'REFACTOR_COUNTING' as AnalysisStateEnum,
        RefactorCounted: 'REFACTOR_COUNTED' as AnalysisStateEnum,
        Finished: 'FINISHED' as AnalysisStateEnum,
        Failed: 'FAILED' as AnalysisStateEnum
    };
    export type LastGoodStateEnum = 'NONE' | 'STARTED' | 'CLONING' | 'CLONED' | 'CHURN_COUNTING' | 'CHURN_COUNTED' | 'LOC_OWNERSHIP_COUNTING' | 'LOC_OWNERSHIP_COUNTED' | 'REFACTOR_COUNTING' | 'REFACTOR_COUNTED' | 'FINISHED' | 'FAILED';
    export const LastGoodStateEnum = {
        None: 'NONE' as LastGoodStateEnum,
        Started: 'STARTED' as LastGoodStateEnum,
        Cloning: 'CLONING' as LastGoodStateEnum,
        Cloned: 'CLONED' as LastGoodStateEnum,
        ChurnCounting: 'CHURN_COUNTING' as LastGoodStateEnum,
        ChurnCounted: 'CHURN_COUNTED' as LastGoodStateEnum,
        LocOwnershipCounting: 'LOC_OWNERSHIP_COUNTING' as LastGoodStateEnum,
        LocOwnershipCounted: 'LOC_OWNERSHIP_COUNTED' as LastGoodStateEnum,
        RefactorCounting: 'REFACTOR_COUNTING' as LastGoodStateEnum,
        RefactorCounted: 'REFACTOR_COUNTED' as LastGoodStateEnum,
        Finished: 'FINISHED' as LastGoodStateEnum,
        Failed: 'FAILED' as LastGoodStateEnum
    };
}



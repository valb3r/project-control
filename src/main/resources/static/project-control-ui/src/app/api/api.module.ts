import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { AliasEntityControllerService } from './api/aliasEntityController.service';
import { AliasPropertyReferenceControllerService } from './api/aliasPropertyReferenceController.service';
import { AliasSearchControllerService } from './api/aliasSearchController.service';
import { FileExclusionRuleEntityControllerService } from './api/fileExclusionRuleEntityController.service';
import { FileExclusionRulePropertyReferenceControllerService } from './api/fileExclusionRulePropertyReferenceController.service';
import { FileExclusionRuleSearchControllerService } from './api/fileExclusionRuleSearchController.service';
import { FileInclusionRuleEntityControllerService } from './api/fileInclusionRuleEntityController.service';
import { FileInclusionRulePropertyReferenceControllerService } from './api/fileInclusionRulePropertyReferenceController.service';
import { FileInclusionRuleSearchControllerService } from './api/fileInclusionRuleSearchController.service';
import { GitRepoEntityControllerService } from './api/gitRepoEntityController.service';
import { GitRepoSearchControllerService } from './api/gitRepoSearchController.service';
import { ProfileControllerService } from './api/profileController.service';
import { RemovedLinesEntityControllerService } from './api/removedLinesEntityController.service';
import { RemovedLinesPropertyReferenceControllerService } from './api/removedLinesPropertyReferenceController.service';
import { RemovedLinesSearchControllerService } from './api/removedLinesSearchController.service';
import { StatisticsSearchControllerService } from './api/statisticsSearchController.service';
import { TotalOwnershipStatsEntityControllerService } from './api/totalOwnershipStatsEntityController.service';
import { TotalOwnershipStatsPropertyReferenceControllerService } from './api/totalOwnershipStatsPropertyReferenceController.service';
import { TotalOwnershipStatsSearchControllerService } from './api/totalOwnershipStatsSearchController.service';
import { UserEntityControllerService } from './api/userEntityController.service';
import { UserPropertyReferenceControllerService } from './api/userPropertyReferenceController.service';
import { UserSearchControllerService } from './api/userSearchController.service';
import { WeeklyCommitStatsEntityControllerService } from './api/weeklyCommitStatsEntityController.service';
import { WeeklyCommitStatsPropertyReferenceControllerService } from './api/weeklyCommitStatsPropertyReferenceController.service';
import { WeeklyCommitStatsSearchControllerService } from './api/weeklyCommitStatsSearchController.service';

@NgModule({
  imports:      [],
  declarations: [],
  exports:      [],
  providers: []
})
export class ApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders<ApiModule> {
        return {
            ngModule: ApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        };
    }

    constructor( @Optional() @SkipSelf() parentModule: ApiModule,
                 @Optional() http: HttpClient) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
        }
        if (!http) {
            throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
            'See also https://github.com/angular/angular/issues/20575');
        }
    }
}

import {AfterViewInit, Component, Input} from '@angular/core';
import {
  AliasSearchControllerService,
  EntityModelAlias,
  EntityModelGitRepo,
  EntityModelUser,
  UserEntityControllerService,
  UserSearchControllerService
} from "../../api";
import {Id} from "../../id";
import {zip} from "rxjs";

@Component({
  selector: 'app-alias-to-user-list',
  templateUrl: './user-to-alias.component.html',
  styleUrls: ['./user-to-alias.component.scss']
})
export class UserToAliasComponent implements AfterViewInit {

  @Input() project: EntityModelGitRepo;

  aliases: EntityModelAlias[];
  users: EntityModelUser[];

  isLoadingResults = true;

  constructor(private aliasSearch: AliasSearchControllerService, private userSearch: UserSearchControllerService, private usersController: UserEntityControllerService) {
  }

  ngAfterViewInit(): void {
    const repoId = +Id.read(this.project._links.self.href);
    const aliases = this.aliasSearch.executeSearchAliasGet1(repoId);
    const users = this.userSearch.executeSearchUserGet1(repoId);

    zip(aliases, users).subscribe(pair => {
      this.aliases = pair[0]._embedded.aliases;
      this.users = pair[1]._embedded.users
      this.isLoadingResults = false
    });
  }
}

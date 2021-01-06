import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {
  AliasSearchControllerService,
  EntityModelAlias,
  EntityModelGitRepo,
  EntityModelUser, User,
  UserEntityControllerService,
  UserSearchControllerService
} from "../../api";
import {Id} from "../../id";
import {Observable, zip} from "rxjs";
import {FormControl, Validators} from "@angular/forms";
import {debounceTime, distinctUntilChanged, flatMap, map, mergeMap, startWith, switchMap} from "rxjs/operators";
import {MatListOption, MatSelectionList} from "@angular/material/list";

@Component({
  selector: 'app-alias-to-user-list',
  templateUrl: './user-to-alias.component.html',
  styleUrls: ['./user-to-alias.component.scss']
})
export class UserToAliasComponent implements AfterViewInit {
  @Input() project: EntityModelGitRepo;

  @ViewChild("aliasesSelected") aliasesSelected: MatSelectionList;

  findSelectedUser: EntityModelUser;
  aliases: EntityModelAlias[];
  users: EntityModelUser[];
  filteredSearchUsers: Observable<EntityModelUser[]>;
  isLoadingResults = true;
  modes = Mode;
  mode = Mode.NONE

  existingUserName = new FormControl('');
  newUserName = new FormControl('', [Validators.required, Validators.minLength(3)]);

  private repoId: number;

  constructor(private aliasSearch: AliasSearchControllerService, private userSearch: UserSearchControllerService, private usersController: UserEntityControllerService) {
  }

  ngAfterViewInit(): void {
    this.filteredSearchUsers = this.existingUserName.valueChanges
      .pipe(
        startWith(''),
        debounceTime(400),
        distinctUntilChanged(),
        switchMap(val => {
          return this.filter(val || '')
        })
      );

    this.repoId = +Id.read(this.project._links.self.href);
    this.readUsersAndAliases();
  }

  toggleSearchUserMode() {
    if (this.mode === Mode.SEARCH_USER) {
      this.mode = Mode.NONE;
      return;
    }

    this.mode = Mode.SEARCH_USER;
  }

  toggleAddUserMode() {
    if (this.mode === Mode.ADD_USER) {
      this.mode = Mode.NONE;
      return;
    }

    this.mode = Mode.ADD_USER;
  }

  displayFn(user?: EntityModelUser): string | undefined {
    return user ? user.name : undefined;
  }

  createNewUser() {
    if (!this.newUserName.valid) {
      return
    }

    this.usersController.postCollectionResourceUserPost({name: this.newUserName.value} as User).pipe(
      mergeMap(_ => this.userSearch.executeSearchUserGet1(this.repoId))
    ).subscribe(res => this.users = res._embedded.users);
  }

  findUserSelected(user: EntityModelUser) {
    this.findSelectedUser = user;
  }

  assignAliasesToUser(user: EntityModelUser) {
    if (!this.aliasesSelected.selectedOptions.selected || this.aliasesSelected.selectedOptions.selected.length === 0) {
      return;
    }

    this.usersController.patchItemResourceUserPatch(
      Id.read(user._links.self.href),
      {aliases: this.aliasesSelected.selectedOptions.selected.map(it => it.value as EntityModelAlias).map(it => it._links.self.href)} as any
    ).subscribe(res => {
      this.findSelectedUser = undefined;
      this.readUsersAndAliases();
    })
  }

  private filter(name: string): Observable<EntityModelUser[]> {
    return this.userSearch.executeSearchUserGet(name ? name + "*" : "*")
      .pipe(
        map(response => response._embedded.users.filter(option => {
          return option.name.toLowerCase().indexOf(name.toLowerCase()) === 0
        }))
      )
  }

  private readUsersAndAliases() {
    const aliases = this.aliasSearch.executeSearchAliasGet1(this.repoId);
    const users = this.userSearch.executeSearchUserGet1(this.repoId);

    zip(aliases, users).subscribe(pair => {
      this.users = pair[1]._embedded.users;
      const definedAliases = new Set<string>();
      this.users.reduce((acc: EntityModelAlias[], val) => val.aliases ? acc.concat(val.aliases) : acc.concat(), [])
        .forEach(it => definedAliases.add(it.name));
      this.aliases = pair[0]._embedded.aliases.filter(it => !definedAliases.has(it.name));
      this.isLoadingResults = false;
    });
  }
}

enum Mode {
  NONE = 'NONE',
  SEARCH_USER = 'SEARCH',
  ADD_USER = 'ADD'
}

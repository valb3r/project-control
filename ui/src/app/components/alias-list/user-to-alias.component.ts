import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {
  AliasSearchControllerService,
  EntityModelAlias,
  EntityModelGitRepo,
  EntityModelUser,
  User,
  UserEntityControllerService,
  UserPropertyReferenceControllerService,
  UserSearchControllerService
} from "../../api";
import {Id} from "../../id";
import {Observable, zip} from "rxjs";
import {FormControl, Validators} from "@angular/forms";
import {debounceTime, distinctUntilChanged, map, startWith, switchMap} from "rxjs/operators";
import {MatSelectionList} from "@angular/material/list";

@Component({
  selector: 'app-alias-to-user-list',
  templateUrl: './user-to-alias.component.html',
  styleUrls: ['./user-to-alias.component.scss']
})
export class UserToAliasComponent implements AfterViewInit {
  @Input() project: EntityModelGitRepo;

  @ViewChild("aliasesSelected") aliasesSelected: MatSelectionList;

  inMemorySelectedUser: EntityModelUser;
  aliases: EntityModelAlias[];
  users: EntityModelUser[];
  filteredSearchUsers: Observable<EntityModelUser[]>;
  isLoadingResults = true;
  modes = Mode;
  mode = Mode.NONE;
  expandedElement: EntityModelUser;

  existingUserName = new FormControl('');
  newUserName = new FormControl('', [Validators.required, Validators.minLength(3)]);

  private repoId: number;

  constructor(
    private aliasSearch: AliasSearchControllerService,
    private userSearch: UserSearchControllerService,
    private usersController: UserEntityControllerService,
    private userToAlias: UserPropertyReferenceControllerService
  ) {
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

    if (!this.aliasesSelected.selectedOptions.isEmpty()) {
      this.newUserName.setValue((this.aliasesSelected.selectedOptions.selected[0].value as EntityModelAlias).name);
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

    zip(
      this.usersController.postCollectionResourceUserPost({name: this.newUserName.value} as User),
      this.userSearch.findByRepoIdL(this.repoId)
    ).subscribe(res => {
      this.users = res[1]._embedded.users;
      this.inMemorySelectedUser = res[0];
    });
  }

  findUserSelected(user: EntityModelUser) {
    this.inMemorySelectedUser = user;
  }

  removeUser(user: EntityModelUser) {
    this.usersController.deleteItemResourceUserDelete(Id.read(user._links.self.href))
      .subscribe(_ => this.readUsersAndAliases());
  }

  removeAlias(user: EntityModelUser, alias: EntityModelAlias) {
    this.userToAlias.deletePropertyReferenceIdUserDelete(Id.read(user._links.self.href), "" + alias.id)
      .subscribe(_ => this.readUsersAndAliases());
  }

  assignAliasesToUser(user: EntityModelUser) {
    if (!this.aliasesSelected.selectedOptions.selected || this.aliasesSelected.selectedOptions.selected.length === 0) {
      return;
    }

    let aliases = this.aliasesSelected.selectedOptions.selected.map(it => it.value as EntityModelAlias).map(it => it._links.self.href);
    aliases = aliases.concat(user.aliases ? user.aliases.map(it => it as any).map(it => it?._links?.self?.href) : []);
    this.usersController.patchItemResourceUserPatch(
      Id.read(user._links.self.href),
      {aliases: aliases} as any
    ).subscribe(res => {
      this.inMemorySelectedUser = undefined;
      this.readUsersAndAliases();
    })
  }

  expandOrCollapseRow(item: EntityModelUser) {
    if (this.expandedElement === item) {
      this.expandedElement = null;
      return;
    }

    this.expandedElement = item;
  }

  private filter(name: string): Observable<EntityModelUser[]> {
    if (typeof name !== "string") {
      name = '';
    }

    return this.userSearch.findByFullTextS(name ? name + "*" : "*")
      .pipe(
        map(response => response._embedded.users.filter(option => {
          return option.name.toLowerCase().indexOf(name.toLowerCase()) === 0
        }))
      )
  }

  private readUsersAndAliases() {
    const aliases = this.aliasSearch.findByRepoIdL(this.repoId);
    const users = this.userSearch.findByRepoIdL(this.repoId);

    zip(aliases, users).subscribe(pair => {
      this.users = pair[1]._embedded.users;
      const definedAliases = new Set<string>();
      this.users.reduce((acc: EntityModelAlias[], val) => val.aliases ? acc.concat(val.aliases) : acc.concat(), [])
        .forEach(it => definedAliases.add(it.name));
      this.aliases = pair[0]._embedded.aliases.filter(it => !definedAliases.has(it.name));
      this.isLoadingResults = false;
    });

    this.existingUserName.setValue('');
  }
}

enum Mode {
  NONE = 'NONE',
  SEARCH_USER = 'SEARCH',
  ADD_USER = 'ADD'
}

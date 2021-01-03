import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {GitRepo, GitRepoEntityControllerService} from "../../api";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-add-project-dialog',
  templateUrl: './add-project-dialog.component.html',
  styleUrls: ['./add-project-dialog.component.scss']
})
export class AddProjectDialogComponent implements OnInit {

  name = new FormControl('', [Validators.required, Validators.minLength(3)]);
  url = new FormControl('', [Validators.required, Validators.minLength(3)]);
  branch = new FormControl('', [Validators.required, Validators.minLength(3)]);
  isPublic = new FormControl(true);

  form: FormGroup = new FormGroup({
    name: this.name,
    url: this.url,
    branch: this.branch
  });

  constructor(private gitRepoes: GitRepoEntityControllerService, public dialogRef: MatDialogRef<AddProjectDialogComponent>) {}

  ngOnInit(): void {
  }

  onAddClick(): void {
    if (!this.form.valid) {
      return
    }

    this.gitRepoes.postCollectionResourceGitrepoPost(
      {
        name: this.name.value,
        url: this.url.value,
        branchToAnalyze: this.branch.value,
        needsAuthentication: this.isPublic.value
      } as GitRepo
    ).subscribe(resp => {
      this.dialogRef.close(resp);
    }, error => {
      console.log("Error", error);
    });
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }
}

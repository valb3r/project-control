import {Injectable} from '@angular/core';
import {MatPaginatorIntl} from "@angular/material/paginator";

@Injectable()
export class MatPaginatorIntlWithoutRange extends MatPaginatorIntl {

  getRangeLabel = (page:number, pageSize:number, length:number)=>{
    return ''
  }
}

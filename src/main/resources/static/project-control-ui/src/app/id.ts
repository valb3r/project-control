export class Id {
  public static read(hateosUrl: string): string {
    const split = hateosUrl.split("/");
    return split[split.length - 1];
  }
}

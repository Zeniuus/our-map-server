export interface RunSqlResult {
  columns: string[];
  rows: string[][];
}

export interface ClubQuestCreateParams {
  title: string;
  rows: ClubQuestCreateParamsInputRow[];
}

export interface ClubQuestCreateParamsInputRow {
  lng: number;
  lat: number;
  displayedName: string;
  placeName: string;
}

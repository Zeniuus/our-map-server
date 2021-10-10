interface RunSqlResult {
  columns: string[];
  rows: string[][];
}

interface ClubQuest {
  id: string;
  title: string;
  content: ClubQuestContent;
}

interface ClubQuestContent {
  targets: Array<{
    lng: number;
    lat: number;
    displayedName: string;
  }>;
}

interface ClubRequestCreateParams {
  title: string;
  content: ClubQuestContent;
}

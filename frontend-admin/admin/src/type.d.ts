export interface LocationDTO {
  lng: number;
  lat: number;
}

export interface ClubQuestDTO {
  id: string;
  title: string;
  content: ClubQuestContentDTO;
}

export interface ClubQuestContentDTO {
  targets: ClubQuestContentTargetDTO[];
}

export interface ClubQuestContentTargetDTO {
  lng: number;
  lat: number;
  displayedName: string;
  places: ClubQuestContentTargetPlaceDTO[];
}

export interface ClubQuestContentTargetPlaceDTO {
  name: string;
  isCompleted: boolean;
  isClosed: boolean;
  isNotAccessible: boolean;
}

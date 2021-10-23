import axios from 'axios';
import { ClubQuestCreateParams, RunSqlResult } from './api';
import { ClubQuestContentTargetDTO, ClubQuestContentTargetPlaceDTO, ClubQuestDTO } from "./type";
import { downloadAttachment } from './util/downloadAttachment';

const baseURL = ((window as any).ENV || {}).REACT_APP_API_BASE_URL || 'http://localhost:8081/api';

const apiClient = axios.create({
  baseURL,
});

apiClient.interceptors.response.use((response) => response, (error) => {
  const res = error.response;
  if (res == null) {
    alert('알 수 없는 문제가 발생했습니다.');
    throw error;
  }
  const msg = res.data?.msg;
  if (msg == null) {
    alert('알 수 없는 문제가 발생했습니다.');
    throw error;
  }
  alert(msg);
  throw error;
});

export const apiController = {
  createClubQuest(params: ClubQuestCreateParams): Promise<ClubQuestDTO> {
    return apiClient.post("/clubQuests/create", params)
      .then(res => res.data);
  },

  getClubQuest(id: string): Promise<ClubQuestDTO> {
    return apiClient.get(`/clubQuests/${id}`)
      .then(res => res.data);
  },

  listClubQuests(): Promise<ClubQuestDTO[]> {
    return apiClient.get("/clubQuests")
      .then(res => res.data);
  },

  setPlaceIsCompleted(id: string, target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO): Promise<ClubQuestDTO> {
    return apiClient.post(`/clubQuests/${id}/setPlaceIsCompleted/${!place.isCompleted}`, {
      lng: target.lng,
      lat: target.lat,
      targetDisplayedName: target.displayedName,
      placeName: place.name,
    }).then(res => res.data);
  },

  setPlaceIsClosed(id: string, target: ClubQuestContentTargetDTO, place: ClubQuestContentTargetPlaceDTO): Promise<ClubQuestDTO> {
    return apiClient.post(`/clubQuests/${id}/setPlaceIsClosed/${!place.isClosed}`, {
      lng: target.lng,
      lat: target.lat,
      targetDisplayedName: target.displayedName,
      placeName: place.name,
    }).then(res => res.data);
  },

  deleteClubQuest(id: string): Promise<void> {
    return apiClient.get(`/clubQuests/${id}/delete`);
  },

  runSql(query: string): Promise<RunSqlResult> {
    return apiClient.post('/runSql', { query })
      .then(res => res.data);
  },

  downloadSqlResultAsTsv(query: string): Promise<void> {
    return apiClient.post('/downloadSqlResultAsTsv', { query })
      .then(res => downloadAttachment(res));
  },
};

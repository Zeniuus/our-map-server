import { Button, ButtonGroup, Dialog } from '@blueprintjs/core';
import React, { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import apiClient from '../../apiClient';
import CreateClubQuest from './CreateClubQuest';
import { ClubQuestDTO } from '../../type';

import './ClubQuests.scss';

function ClubQuests() {
  const [isLoading, setIsLoading] = useState(false);
  const [clubQuests, setClubQuests] = useState<ClubQuestDTO[]>([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  useEffect(() => {
    listClubQuests();
  }, []);

  function withLoading(block: () => Promise<any>): Promise<any> {
    setIsLoading(true);
    return block().finally(() => {
      setIsLoading(false);
    });
  }

  function listClubQuests(): Promise<any> {
    return withLoading(() => {
      return apiClient.get("/clubQuests")
        .then((res) => {
          const clubRequests: ClubQuestDTO[] = res.data;
          setClubQuests(clubRequests)
        });
    });
  }

  function deleteClubQuest(id: string): Promise<any> {
    return withLoading(() => {
      return apiClient.get(`/clubQuests/${id}/delete`)
        .then(() => {
          return listClubQuests();
        });
    });
  }

  function onClubQuestCreateBtnClick() {
    setIsCreateDialogOpen(true);
  }

  function onClubQuestCreated() {
    setIsCreateDialogOpen(false);
    listClubQuests();
  }

  const history = useHistory();
  function onClubQuestClick(clubQuest: ClubQuestDTO) {
    return () => {
      history.push(`/clubQuests/${clubQuest.id}`);
    };
  }

  function onClubQuestDeleteBtnClick(clubQuest: ClubQuestDTO) {
    return (e: React.MouseEvent) => {
      e.stopPropagation();
      if (!window.confirm(`정말 ${clubQuest.title} 퀘스트를 삭제하시겠습니까?`)) {
        return;
      }
      deleteClubQuest(clubQuest.id);
    };
  }

  return (
    <div>
      <h1>퀘스트</h1>
      <ButtonGroup alignText="right">
        <Button text="새 퀘스트 생성" onClick={onClubQuestCreateBtnClick}></Button>
      </ButtonGroup>
      <table className="club-quests bp3-html-table bp3-html-table-bordered bp3-html-table-condensed bp3-interactive">
        <thead>
          <tr>
            <th className="title-column">퀘스트 이름</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          {clubQuests.map((clubQuest) => {
            return (
              <tr onClick={onClubQuestClick(clubQuest)}>
                <td>{clubQuest.title}</td>
                <td><Button icon="trash" disabled={isLoading} onClick={onClubQuestDeleteBtnClick(clubQuest)} /></td>
              </tr>
            )
          })}
        </tbody>
      </table>
      <Dialog className="create-dialog" title="퀘스트 생성하기" isOpen={isCreateDialogOpen}>
        <CreateClubQuest onCreate={onClubQuestCreated}></CreateClubQuest>
      </Dialog>
    </div>
  );
}

export default ClubQuests;

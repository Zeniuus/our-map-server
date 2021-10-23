import { Button, ButtonGroup, Dialog } from '@blueprintjs/core';
import React, { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import CreateClubQuest from './CreateClubQuest';
import { ClubQuestDTO } from '../../type';
import { apiController } from '../../apiController';

import './ClubQuests.scss';

function ClubQuests() {
  const [isLoading, setIsLoading] = useState(false);
  const [clubQuests, setClubQuests] = useState<ClubQuestDTO[]>([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  function withLoading(promise: Promise<any>): Promise<any> {
    setIsLoading(true);
    return promise.finally(() => setIsLoading(false));
  }

  useEffect(() => {
    withLoading(
      apiController.listClubQuests()
        .then(clubQuests => setClubQuests(clubQuests))
    );
  }, []);

  function onClubQuestCreateBtnClick() {
    setIsCreateDialogOpen(true);
  }

  function onClubQuestCreated() {
    setIsCreateDialogOpen(false);
    withLoading(
      apiController.listClubQuests()
        .then(clubQuests => setClubQuests(clubQuests))
    );
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
      withLoading(
        apiController.deleteClubQuest(clubQuest.id)
          .then(() => {
            return apiController.listClubQuests()
          })
          .then(clubQuests => setClubQuests(clubQuests))
      );
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

import React, { useState } from 'react';
import { Button, ButtonGroup, Classes } from '@blueprintjs/core';
import apiClient from '../../apiClient';
import { ClubQuestCreateParams } from '../../api';

import './CreateClubQuest.scss';

interface CreateClubQuestProps {
  onCreate: () => void;
}

function CreateClubQuest(props: CreateClubQuestProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [title, setTitle] = useState('');
  const [rawContent, setRawContent] = useState('');

  function withLoading(block: () => Promise<any>) {
    setIsLoading(true);
    block().finally(() => {
      setIsLoading(false);
    });
  }

  function createClubQuest(rows: ClubQuestCreateParams) {
    withLoading(() => {
      return apiClient.post("/clubQuests/create", rows)
        .then(() => {
          props.onCreate();
        });
    });
  }

  function onCreateClubQuest() {
    const rows = rawContent.split('\n')
      .map(it => it.split('\t'))
      .filter(it => it.length === 4)
      .map((tokens) => {
        return {
          lng: Number(tokens[0]),
          lat: Number(tokens[1]),
          displayedName: tokens[3],
          placeName: tokens[2],
        };
      });
    if (rows.length === 0) {
      alert('입력된 줄 중 유효한 줄이 없습니다. 입력한 값을 다시 확인해주세요.');
      return;
    }
    createClubQuest({
      title,
      rows,
    });
  }

  function isInputValid(): boolean {
    return title.trim() !== '' && rawContent.trim() !== '';
  }

  return (
    <div className="root">
      <div className="input-container">
        <div className="title">퀘스트 제목</div>
        <input
          className={`${Classes.INPUT} input bp3-fill`}
          value={title}
          onChange={({ target: { value }}) => setTitle(value)}
        />
      </div>
      <div className="input-container">
        <div className="title">퀘스트 내용</div>
        <textarea
          className={`${Classes.INPUT} input bp3-fill`}
          value={rawContent}
          placeholder="경도<tab>위도<tab>지도 마커 이름; e.g. 127.125902  37.419461  성남시청"
          onChange={({ target: { value }}) => setRawContent(value)}
        />
      </div>
      <ButtonGroup>
        <Button text="생성" disabled={isLoading || !isInputValid()} onClick={onCreateClubQuest} />
      </ButtonGroup>
    </div>
  );
}

export default CreateClubQuest;

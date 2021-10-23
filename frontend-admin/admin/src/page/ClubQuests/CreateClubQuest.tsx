import React, { useState } from 'react';
import { Button, ButtonGroup, Classes } from '@blueprintjs/core';
import { apiController } from '../../apiController';

import './CreateClubQuest.scss';

interface CreateClubQuestProps {
  onCreate: () => void;
}

function CreateClubQuest(props: CreateClubQuestProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [title, setTitle] = useState('');
  const [rawContent, setRawContent] = useState('');

  function withLoading(promise: Promise<any>): Promise<any> {
    setIsLoading(true);
    return promise.finally(() => setIsLoading(false));
  }

  function onCreateClubQuest() {
    const rows = rawContent.split('\n')
      .map(it => it.split('\t'))
      .filter(it => it.length === 4)
      .map((tokens) => {
        return {
          lng: Number(tokens[0]),
          lat: Number(tokens[1]),
          displayedName: tokens[2],
          placeName: tokens[3],
        };
      });
    if (rows.length === 0) {
      alert('입력된 줄 중 유효한 줄이 없습니다. 입력한 값을 다시 확인해주세요.');
      return;
    }

    withLoading(
      apiController.createClubQuest({ title, rows })
        .then(() => props.onCreate())
    );
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
          placeholder="경도<tab>위도<tab>지도 마커 이름<tab>장소 이름; e.g. 127.125902    37.419461    H조 1호 건물    김가네낙지마을"
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

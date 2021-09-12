import React, { useState } from 'react';
import { Button, ButtonGroup, Card, Intent, TextArea } from '@blueprintjs/core';
import apiClient from '../../apiClient';
import { downloadAttachment } from '../../util/downloadAttachment';

import './RunSql.scss';


function RunSql() {
  const [isLoading, setIsLoading] = useState(false);
  const [query, setQuery] = useState('');
  const [queryResult, setQueryResult] = useState<RunSqlResult | null>(null);

  function withLoading(block: () => Promise<any>) {
    setIsLoading(true);
    block().finally(() => {
      setIsLoading(false);
    });
  }

  function runSql() {
    withLoading(() => {
      return apiClient.post('/runSql', {
        query,
      }).then((res) => {
        const result: RunSqlResult = res.data;
        setQueryResult(result);
      });
    });
  }

  function downloadSqlResultAsTsv() {
    withLoading(() => {
      return apiClient.post('/downloadSqlResultAsTsv', {
        query,
      }).then((res) => {
        downloadAttachment(res);
      });
    });
  }

  function handleKeyPress(e: any) {
    if (e.key === 'Enter' && e.ctrlKey) {
      runSql();
    }
  }

  const queryResultElem = queryResult != null
    ? (
      <Card className="query-result-container">
        <h4>결과</h4>
        <table className="bp3-html-table bp3-html-table-bordered bp3-html-table-condensed bp3-interactive">
          <thead>
            <tr>
            {queryResult.columns.map((column) => {
              return <th>{column}</th>;
            })}
            </tr>
          </thead>
          <tbody>
            {queryResult.rows.map((row) => {
              return (
                <tr>
                  {queryResult.columns.map((column, columnIdx) => {
                    return <td>{row[columnIdx]}</td>;
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>
      </Card>
    )
    : null;

  return (
    <div>
      <h1>SQL 실행</h1>
      <ButtonGroup className="button-group" minimal={true}>
        <Button icon="play" disabled={isLoading} text="실행" onClick={runSql}></Button>
        <Button icon="download" disabled={isLoading} text="쿼리 결과 다운로드" onClick={downloadSqlResultAsTsv}></Button>
      </ButtonGroup>
      <TextArea
        className="bp3-fill"
        growVertically={true}
        intent={Intent.PRIMARY}
        spellCheck={false}
        placeholder="Ctrl + Enter로 쿼리를 실행할 수 있습니다."
        onChange={({ target: { value }}) => setQuery(value)}
        onKeyPress={handleKeyPress}
        value={query}
      />
      {queryResultElem}
    </div>
  );
}

export default RunSql;

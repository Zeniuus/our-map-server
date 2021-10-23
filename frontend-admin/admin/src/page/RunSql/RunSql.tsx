import React, { useState, useEffect } from 'react';
import { Button, ButtonGroup, Card, Intent, TextArea } from '@blueprintjs/core';
import { RunSqlResult } from '../../api';

import './RunSql.scss';
import { apiController } from '../../apiController';


function RunSql() {
  const [isLoading, setIsLoading] = useState(false);
  const [query, setQuery] = useState('');
  const [queryResult, setQueryResult] = useState<RunSqlResult | null>(null);
  const [tableNames, setTableNames] = useState<string[]>([]);

  useEffect(() => {
    apiController.runSql('show tables;')
      .then((result) => {
        setTableNames(result.rows.map((row) => row[0]).filter((row) => !row.startsWith('DATABASECHANGELOG')));
      });
  }, []);

  function withLoading(promise: Promise<any>): Promise<any> {
    setIsLoading(true);
    return promise.finally(() => setIsLoading(false));
  }

  function handleKeyPress(e: any) {
    if (e.key === 'Enter' && e.ctrlKey) {
      runSqlWithInputQuery();
    }
  }

  const runSqlWithInputQuery = () => {
    withLoading(
      apiController.runSql(query)
        .then((result) => setQueryResult(result))
    );
  };

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
      <div className="body">
        <div className="sidebar">
          <h4>테이블 목록</h4>
          <ButtonGroup vertical={true} minimal={true} alignText="left">
            {
              tableNames.map((tableName) => {
                function addSelectQuery() {
                  setQuery(query + `${query == '' ? '' : '\n'}SELECT * FROM ${tableName} limit 100;`);
                }
                return (
                  <Button disabled={isLoading} text={tableName} onClick={addSelectQuery}></Button>
                )
              })
            }
          </ButtonGroup>
        </div>
        <div className="main">
          <ButtonGroup className="button-group" minimal={true}>
            <Button icon="play" disabled={isLoading} text="실행" onClick={runSqlWithInputQuery}></Button>
            <Button icon="download" disabled={isLoading} text="쿼리 결과 다운로드" onClick={() => { apiController.downloadSqlResultAsTsv(query) }}></Button>
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
      </div>
    </div>
  );
}

export default RunSql;

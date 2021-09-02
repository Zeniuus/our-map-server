import React, { useState } from 'react';
import { Button, Intent, TextArea } from '@blueprintjs/core';
import apiClient from '../apiClient';


function RunSql() {
  const [isLoading, setIsLoading] = useState(false);
  const [query, setQuery] = useState('');
  const [queryResult, setQueryResult] = useState<RunSqlResult | null>(null);

  function fetchRunSqlResult() {
    setIsLoading(true);
    apiClient.post('/runSql', {
      query,
    }).then((res) => {
      const result: RunSqlResult = res.data;
      setQueryResult(result);
      setIsLoading(false);
    }, () => {
      setIsLoading(false);
    })
  }

  const queryResultElem = queryResult != null
    ? (
      <table className="bp3-html-table bp3-html-table-bordered bp3-html-table-condensed">
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
    )
    : null;

  return (
    <div>
      <h1>SQL 실행</h1>
      <Button disabled={isLoading} text="실행" onClick={fetchRunSqlResult}></Button>
      <br />
      <TextArea
        growVertically={true}
        intent={Intent.PRIMARY}
        onChange={({ target: { value }}) => setQuery(value)}
        value={query}
      />
      {queryResultElem}
    </div>
  );
}

export default RunSql;

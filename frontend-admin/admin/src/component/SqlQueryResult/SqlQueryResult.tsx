import React from 'react';
import { Card } from '@blueprintjs/core';
import { RunSqlResult } from '../../api';

import './SqlQueryResult.scss';

interface SqlQueryResultProps {
  queryResult: RunSqlResult;
}

function SqlQueryResult(props: SqlQueryResultProps) {
  const { queryResult } = props;
  return queryResult != null
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
}

export default SqlQueryResult;

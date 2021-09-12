import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Home from './page/Home';
import RunSql from './page/RunSql/RunSql';

import './AppBody.scss';

const AppBody = () => (
  <div className="app-body">
    <Switch>
      <Route path="/runSql">
        <RunSql></RunSql>
      </Route>
      <Route path="/">
        <Home></Home>
      </Route>
    </Switch>
  </div>
);

export default AppBody;

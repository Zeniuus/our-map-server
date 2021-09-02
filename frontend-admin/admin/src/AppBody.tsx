import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Home from './page/Home';
import RunSql from './page/RunSql';

const AppBody = () => (
  <Switch>
    <Route path="/runSql">
      <RunSql></RunSql>
    </Route>
    <Route path="/">
      <Home></Home>
    </Route>
  </Switch>
);

export default AppBody;

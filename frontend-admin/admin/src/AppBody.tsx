import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Home from './page/Home';
import RunSql from './page/RunSql/RunSql';
import ClubQuests from './page/ClubQuests/ClubQuests';
import ClubQuest from './page/ClubQuest/ClubQuest';

import './AppBody.scss';

const AppBody = () => (
  <div className="app-body">
    <Switch>
      <Route path="/runSql">
        <RunSql></RunSql>
      </Route>
      <Route path="/clubQuests/:id" component={ClubQuest} />
      <Route path="/clubQuests">
        <ClubQuests></ClubQuests>
      </Route>
      <Route path="/">
        <Home></Home>
      </Route>
    </Switch>
  </div>
);

export default AppBody;

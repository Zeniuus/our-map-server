import React, { useState } from 'react';
import { Route, Switch } from 'react-router-dom';
import apiClient from './apiClient';
import Home from './page/Home';
import RunSql from './page/RunSql/RunSql';
import ClubQuests from './page/ClubQuests/ClubQuests';
import ClubQuest from './page/ClubQuest/ClubQuest';
import AuthRoute from './AuthRoute';
import Login from './page/Login/Login';

import './AppBody.scss';

function AppBody() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  function login(userId: string, password: string): Promise<any> {
    if (userId === 'admin' && password === '2021stair!') { // TODO: ㅋㅋㅋㅋ 완전 땜빵
      return Promise.resolve()
        .then(() => {
          setIsAuthenticated(true);
        });
    } else {
      return Promise.reject();
    }
    // return apiClient.post('/login', {
    //   userId,
    //   password,
    // }).then(() => {
    //   setIsAuthenticated(true);
    // }, () => {
    //   alert('로그인에 실패했습니다.');
    // });
  }

  return (
    <div className="app-body">
      <Switch>
        <Route path="/login">
          <Login login={login} isAuthenticated={isAuthenticated}></Login>
        </Route>
        <AuthRoute isAuthenticated={isAuthenticated} path="/runSql" component={RunSql} />
        <Route path="/clubQuests/:id" component={ClubQuest} />
        <AuthRoute isAuthenticated={isAuthenticated} path="/clubQuests" component={ClubQuests} />
        <AuthRoute isAuthenticated={isAuthenticated} path="/" component={Home} />
      </Switch>
    </div>
  );
}

export default AppBody;

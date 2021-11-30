import React, { useState } from 'react';
import { Classes } from "@blueprintjs/core";
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import AppMenu from "./AppMenu";
import HomePage from './page/Home/HomePage';
import RunSqlPage from './page/RunSql/RunSqlPage';
import ClubQuestsPage from './page/ClubQuests/ClubQuestsPage';
import ClubQuestPage from './page/ClubQuest/ClubQuestPage';
import AuthRoute from './AuthRoute';
import LoginPage from './page/Login/LoginPage';

import "./App.scss";
import DashboardPage from './page/Dashboard/DashboardPage';

function App() {
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

  const AppBody = (Child: any, otherProps: any) => {
    return (props: any) => (
      <div className="app-body">
        <Child {...props} {...otherProps} />
      </div>
    );
  }

  return (
    <div className={Classes.RUNNING_TEXT}>
      <BrowserRouter>
        <Switch>
          <Route exact path="/login" component={AppBody(LoginPage, { login, isAuthenticated })} />
          <Route exact path="/clubQuests/:id" component={AppBody(ClubQuestPage, {})} />
          <Route>
            <AppMenu />
            <div className="app-body">
              <Switch>
                <AuthRoute isAuthenticated={isAuthenticated} path="/dashboard" component={DashboardPage} />
                <AuthRoute isAuthenticated={isAuthenticated} path="/runSql" component={RunSqlPage} />
                <AuthRoute isAuthenticated={isAuthenticated} path="/clubQuests" component={ClubQuestsPage} />
                <AuthRoute isAuthenticated={isAuthenticated} path="/" component={HomePage} />
              </Switch>
            </div>
          </Route>
        </Switch>
      </BrowserRouter>
    </div>
  );
}

export default App;

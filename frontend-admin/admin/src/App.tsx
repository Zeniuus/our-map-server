import React, { useState } from 'react';
import { Classes } from "@blueprintjs/core";
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import AppMenu from "./AppMenu";
import Home from './page/Home/Home';
import RunSql from './page/RunSql/RunSql';
import ClubQuests from './page/ClubQuests/ClubQuests';
import ClubQuest from './page/ClubQuest/ClubQuest';
import AuthRoute from './AuthRoute';
import Login from './page/Login/Login';

import "./App.scss";
import Dashboard from './page/Dashboard/Dashboard';

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
          <Route exact path="/login" component={AppBody(Login, { login, isAuthenticated })} />
          <Route exact path="/clubQuests/:id" component={AppBody(ClubQuest, {})} />
          <Route>
            <AppMenu />
            <div className="app-body">
              <Switch>
                <AuthRoute isAuthenticated={isAuthenticated} path="/dashboard" component={Dashboard} />
                <AuthRoute isAuthenticated={isAuthenticated} path="/runSql" component={RunSql} />
                <AuthRoute isAuthenticated={isAuthenticated} path="/clubQuests" component={ClubQuests} />
                <AuthRoute isAuthenticated={isAuthenticated} path="/" component={Home} />
              </Switch>
            </div>
          </Route>
        </Switch>
      </BrowserRouter>
    </div>
  );
}

export default App;

import React, { useState } from 'react';
import { Button } from '@blueprintjs/core';
import { Redirect } from 'react-router-dom';

interface LoginProps {
  login: (userId: string, password: string) => Promise<any>;
  isAuthenticated: boolean;
}

function Login(props: LoginProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');

  if (props.isAuthenticated) {
    return <Redirect to="/" />;
  }

  function login() {
    setIsLoading(true);
    props.login(userId, password)
      .finally(() => {
        setIsLoading(false);
      });
  }

  return (
    <div>
      <div>
        <span>아이디 :</span>
        <input value={userId} onChange={({ target: { value }}) => setUserId(value)} />
      </div>
      <div>
        <span>패스워드 :</span>
        <input type="password" value={password} onChange={({ target: { value }}) => setPassword(value)} />
      </div>
      <Button text="로그인" onClick={login} disabled={isLoading}></Button>
    </div>
  );
}

export default Login;

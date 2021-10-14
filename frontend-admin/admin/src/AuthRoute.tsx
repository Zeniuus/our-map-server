import React from 'react';
import { Route, Redirect } from "react-router-dom";

interface AuthRouteProps {
  isAuthenticated: boolean;
  component: any;
  path: string;
}

function AuthRoute(props: AuthRouteProps) {
  const { isAuthenticated, component: Component, ...rest } = props;
  return (
    <Route
      {...rest}
      render={(props) =>
        isAuthenticated
          ? <Component {...props} />
          : (
            <Redirect
              to={{ pathname: "/login", state: { from: props.location } }}
            />
          )
      }
    />
  );
}

export default AuthRoute;

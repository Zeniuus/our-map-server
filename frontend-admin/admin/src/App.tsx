import React from "react";
import { BrowserRouter } from 'react-router-dom';
import { Classes } from "@blueprintjs/core";
import AppMenu from "./AppMenu";
import AppBody from "./AppBody";

import "./App.scss";

const App = () => (
  <div className={Classes.RUNNING_TEXT}>
    <BrowserRouter>
      <AppMenu></AppMenu>
      <AppBody></AppBody>
    </BrowserRouter>
  </div>
);

export default App;

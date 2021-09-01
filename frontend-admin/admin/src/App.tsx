import * as React from "react";
import { BrowserRouter } from 'react-router-dom';
import AppMenu from "./AppMenu";
import AppBody from "./AppBody";

import "./App.css";

const App = () => (
  <BrowserRouter>
    <AppMenu></AppMenu>
    <AppBody></AppBody>
  </BrowserRouter>
);

export default App;

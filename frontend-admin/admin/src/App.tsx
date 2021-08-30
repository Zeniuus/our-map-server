import * as React from "react";
import { Admin } from "react-admin";
import simpleRestProvider from 'ra-data-simple-rest';

const App = () => <Admin dataProvider={simpleRestProvider('http://localhost:8080')}></Admin>

export default App;

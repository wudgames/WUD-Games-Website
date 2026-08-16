import React from "react";
import { createRoot } from "react-dom/client";
import AppRoutes from './app/routes';
import { initReactI18next } from 'react-i18next';
import i18n from 'i18next';
//import axios from 'axios';

i18n
  .use(initReactI18next)
  .init();

//axios.defaults.baseURL = process.env.API_PATH;

const root = document.getElementById('root')!!;
createRoot(root).render(
  <AppRoutes />
);
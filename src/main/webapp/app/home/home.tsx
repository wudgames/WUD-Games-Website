import React from 'react';
import { useTranslation } from 'react-i18next';
import useDocumentTitle from 'app/common/use-document-title';
//import './home.css';


export default function Home() {
  const { t } = useTranslation();
  useDocumentTitle(t('home.index.headline'));

  return (<>
    <div>WUD Games Website home.tsx</div>
    <div>link to user page</div>
  </>);
}

import React from 'react';
import { Switch } from 'react-router-dom';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import StaticPage from './static-page';
import HelpApp from './help-app';
import ContactUs from './contact-us';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default ({ match }) => {
  return (
    <div>
      <Switch>
        {/* prettier-ignore */}
        <ErrorBoundaryRoute path={`${match.url}static-page`} component={StaticPage} />
        <ErrorBoundaryRoute path={`${match.url}help-app`} component={HelpApp} />
        <ErrorBoundaryRoute path={`${match.url}contact-us`} component={ContactUs} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </Switch>
    </div>
  );
};

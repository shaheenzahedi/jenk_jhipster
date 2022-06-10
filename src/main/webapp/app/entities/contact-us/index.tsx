import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ContactUs from './contact-us';
import ContactUsDetail from './contact-us-detail';
import ContactUsUpdate from './contact-us-update';
import ContactUsDeleteDialog from './contact-us-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ContactUsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ContactUsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ContactUsDetail} />
      <ErrorBoundaryRoute path={match.url} component={ContactUs} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ContactUsDeleteDialog} />
  </>
);

export default Routes;

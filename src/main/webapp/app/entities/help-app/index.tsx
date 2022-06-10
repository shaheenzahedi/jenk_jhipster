import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import HelpApp from './help-app';
import HelpAppDetail from './help-app-detail';
import HelpAppUpdate from './help-app-update';
import HelpAppDeleteDialog from './help-app-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={HelpAppUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={HelpAppUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={HelpAppDetail} />
      <ErrorBoundaryRoute path={match.url} component={HelpApp} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={HelpAppDeleteDialog} />
  </>
);

export default Routes;

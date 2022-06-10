import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import StaticPage from './static-page';
import StaticPageDetail from './static-page-detail';
import StaticPageUpdate from './static-page-update';
import StaticPageDeleteDialog from './static-page-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StaticPageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StaticPageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StaticPageDetail} />
      <ErrorBoundaryRoute path={match.url} component={StaticPage} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StaticPageDeleteDialog} />
  </>
);

export default Routes;

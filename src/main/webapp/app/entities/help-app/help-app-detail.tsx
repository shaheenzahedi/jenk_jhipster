import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './help-app.reducer';

export const HelpAppDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const helpAppEntity = useAppSelector(state => state.helpApp.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="helpAppDetailsHeading">
          <Translate contentKey="wDanakApp.helpApp.detail.title">HelpApp</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="wDanakApp.helpApp.id">Id</Translate>
            </span>
          </dt>
          <dd>{helpAppEntity.id}</dd>
          <dt>
            <span id="staticPageId">
              <Translate contentKey="wDanakApp.helpApp.staticPageId">Static Page Id</Translate>
            </span>
          </dt>
          <dd>{helpAppEntity.staticPageId}</dd>
        </dl>
        <Button tag={Link} to="/help-app" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/help-app/${helpAppEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HelpAppDetail;

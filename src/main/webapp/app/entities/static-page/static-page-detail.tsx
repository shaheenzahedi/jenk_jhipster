import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './static-page.reducer';

export const StaticPageDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const staticPageEntity = useAppSelector(state => state.staticPage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="staticPageDetailsHeading">
          <Translate contentKey="wDanakApp.staticPage.detail.title">StaticPage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="wDanakApp.staticPage.id">Id</Translate>
            </span>
          </dt>
          <dd>{staticPageEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="wDanakApp.staticPage.name">Name</Translate>
            </span>
          </dt>
          <dd>{staticPageEntity.name}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="wDanakApp.staticPage.content">Content</Translate>
            </span>
          </dt>
          <dd>{staticPageEntity.content}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="wDanakApp.staticPage.status">Status</Translate>
            </span>
          </dt>
          <dd>{staticPageEntity.status}</dd>
          <dt>
            <span id="fileId">
              <Translate contentKey="wDanakApp.staticPage.fileId">File Id</Translate>
            </span>
          </dt>
          <dd>{staticPageEntity.fileId}</dd>
          <dt>
            <Translate contentKey="wDanakApp.staticPage.helpApp">Help App</Translate>
          </dt>
          <dd>{staticPageEntity.helpApp ? staticPageEntity.helpApp.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/static-page" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/static-page/${staticPageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StaticPageDetail;

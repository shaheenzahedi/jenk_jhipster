import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './contact-us.reducer';

export const ContactUsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const contactUsEntity = useAppSelector(state => state.contactUs.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="contactUsDetailsHeading">
          <Translate contentKey="wDanakApp.contactUs.detail.title">ContactUs</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="wDanakApp.contactUs.id">Id</Translate>
            </span>
          </dt>
          <dd>{contactUsEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="wDanakApp.contactUs.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{contactUsEntity.userId}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="wDanakApp.contactUs.email">Email</Translate>
            </span>
          </dt>
          <dd>{contactUsEntity.email}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="wDanakApp.contactUs.message">Message</Translate>
            </span>
          </dt>
          <dd>{contactUsEntity.message}</dd>
          <dt>
            <span id="createTime">
              <Translate contentKey="wDanakApp.contactUs.createTime">Create Time</Translate>
            </span>
          </dt>
          <dd>
            {contactUsEntity.createTime ? <TextFormat value={contactUsEntity.createTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/contact-us" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/contact-us/${contactUsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ContactUsDetail;

import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IContactUs } from 'app/shared/model/contact-us.model';
import { getEntity, updateEntity, createEntity, reset } from './contact-us.reducer';

export const ContactUsUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const contactUsEntity = useAppSelector(state => state.contactUs.entity);
  const loading = useAppSelector(state => state.contactUs.loading);
  const updating = useAppSelector(state => state.contactUs.updating);
  const updateSuccess = useAppSelector(state => state.contactUs.updateSuccess);
  const handleClose = () => {
    props.history.push('/contact-us');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createTime = convertDateTimeToServer(values.createTime);

    const entity = {
      ...contactUsEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createTime: displayDefaultDateTime(),
        }
      : {
          ...contactUsEntity,
          createTime: convertDateTimeFromServer(contactUsEntity.createTime),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="wDanakApp.contactUs.home.createOrEditLabel" data-cy="ContactUsCreateUpdateHeading">
            <Translate contentKey="wDanakApp.contactUs.home.createOrEditLabel">Create or edit a ContactUs</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="contact-us-id"
                  label={translate('wDanakApp.contactUs.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('wDanakApp.contactUs.userId')}
                id="contact-us-userId"
                name="userId"
                data-cy="userId"
                type="text"
              />
              <ValidatedField
                label={translate('wDanakApp.contactUs.email')}
                id="contact-us-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('wDanakApp.contactUs.message')}
                id="contact-us-message"
                name="message"
                data-cy="message"
                type="textarea"
              />
              <ValidatedField
                label={translate('wDanakApp.contactUs.createTime')}
                id="contact-us-createTime"
                name="createTime"
                data-cy="createTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/contact-us" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ContactUsUpdate;

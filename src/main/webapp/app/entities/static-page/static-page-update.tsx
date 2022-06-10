import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IHelpApp } from 'app/shared/model/help-app.model';
import { getEntities as getHelpApps } from 'app/entities/help-app/help-app.reducer';
import { IStaticPage } from 'app/shared/model/static-page.model';
import { StaticPageStatus } from 'app/shared/model/enumerations/static-page-status.model';
import { getEntity, updateEntity, createEntity, reset } from './static-page.reducer';

export const StaticPageUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const helpApps = useAppSelector(state => state.helpApp.entities);
  const staticPageEntity = useAppSelector(state => state.staticPage.entity);
  const loading = useAppSelector(state => state.staticPage.loading);
  const updating = useAppSelector(state => state.staticPage.updating);
  const updateSuccess = useAppSelector(state => state.staticPage.updateSuccess);
  const staticPageStatusValues = Object.keys(StaticPageStatus);
  const handleClose = () => {
    props.history.push('/static-page');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getHelpApps({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...staticPageEntity,
      ...values,
      helpApp: helpApps.find(it => it.id.toString() === values.helpApp.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          status: 'DRAFT',
          ...staticPageEntity,
          helpApp: staticPageEntity?.helpApp?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="wDanakApp.staticPage.home.createOrEditLabel" data-cy="StaticPageCreateUpdateHeading">
            <Translate contentKey="wDanakApp.staticPage.home.createOrEditLabel">Create or edit a StaticPage</Translate>
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
                  id="static-page-id"
                  label={translate('wDanakApp.staticPage.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('wDanakApp.staticPage.name')}
                id="static-page-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('wDanakApp.staticPage.content')}
                id="static-page-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('wDanakApp.staticPage.status')}
                id="static-page-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {staticPageStatusValues.map(staticPageStatus => (
                  <option value={staticPageStatus} key={staticPageStatus}>
                    {translate('wDanakApp.StaticPageStatus.' + staticPageStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('wDanakApp.staticPage.fileId')}
                id="static-page-fileId"
                name="fileId"
                data-cy="fileId"
                type="text"
              />
              <ValidatedField
                id="static-page-helpApp"
                name="helpApp"
                data-cy="helpApp"
                label={translate('wDanakApp.staticPage.helpApp')}
                type="select"
              >
                <option value="" key="0" />
                {helpApps
                  ? helpApps.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/static-page" replace color="info">
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

export default StaticPageUpdate;

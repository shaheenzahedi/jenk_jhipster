import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { byteSize, Translate, translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IContactUs } from 'app/shared/model/contact-us.model';
import { searchEntities, getEntities } from './contact-us.reducer';

export const ContactUs = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');

  const contactUsList = useAppSelector(state => state.contactUs.entities);
  const loading = useAppSelector(state => state.contactUs.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(searchEntities({ query: search }));
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="contact-us-heading" data-cy="ContactUsHeading">
        <Translate contentKey="wDanakApp.contactUs.home.title">Contactuses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="wDanakApp.contactUs.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/contact-us/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="wDanakApp.contactUs.home.createLabel">Create new Contact Us</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('wDanakApp.contactUs.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {contactUsList && contactUsList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="wDanakApp.contactUs.id">Id</Translate>
                </th>
                <th>
                  <Translate contentKey="wDanakApp.contactUs.userId">User Id</Translate>
                </th>
                <th>
                  <Translate contentKey="wDanakApp.contactUs.email">Email</Translate>
                </th>
                <th>
                  <Translate contentKey="wDanakApp.contactUs.message">Message</Translate>
                </th>
                <th>
                  <Translate contentKey="wDanakApp.contactUs.createTime">Create Time</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {contactUsList.map((contactUs, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/contact-us/${contactUs.id}`} color="link" size="sm">
                      {contactUs.id}
                    </Button>
                  </td>
                  <td>{contactUs.userId}</td>
                  <td>{contactUs.email}</td>
                  <td>{contactUs.message}</td>
                  <td>{contactUs.createTime ? <TextFormat type="date" value={contactUs.createTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/contact-us/${contactUs.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/contact-us/${contactUs.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/contact-us/${contactUs.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="wDanakApp.contactUs.home.notFound">No Contactuses found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ContactUs;

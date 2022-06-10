import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/static-page">
        <Translate contentKey="global.menu.entities.staticPage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/help-app">
        <Translate contentKey="global.menu.entities.helpApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/contact-us">
        <Translate contentKey="global.menu.entities.contactUs" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu as React.ComponentType<any>;

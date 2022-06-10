package org.aydm.danak.service.mapper

import org.aydm.danak.domain.HelpApp
import org.aydm.danak.service.dto.HelpAppDTO
import org.mapstruct.*

/**
 * Mapper for the entity [HelpApp] and its DTO [HelpAppDTO].
 */
@Mapper(componentModel = "spring")
interface HelpAppMapper :
    EntityMapper<HelpAppDTO, HelpApp>

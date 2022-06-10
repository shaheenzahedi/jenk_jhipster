package org.aydm.danak.service.mapper

import org.aydm.danak.domain.ContactUs
import org.aydm.danak.service.dto.ContactUsDTO
import org.mapstruct.*

/**
 * Mapper for the entity [ContactUs] and its DTO [ContactUsDTO].
 */
@Mapper(componentModel = "spring")
interface ContactUsMapper :
    EntityMapper<ContactUsDTO, ContactUs>

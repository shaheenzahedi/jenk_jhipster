package org.aydm.danak.service.mapper

import org.aydm.danak.domain.HelpApp
import org.aydm.danak.domain.StaticPage
import org.aydm.danak.service.dto.HelpAppDTO
import org.aydm.danak.service.dto.StaticPageDTO
import org.mapstruct.*

/**
 * Mapper for the entity [StaticPage] and its DTO [StaticPageDTO].
 */
@Mapper(componentModel = "spring")
interface StaticPageMapper :
    EntityMapper<StaticPageDTO, StaticPage> {

    @Mappings(
        Mapping(target = "helpApp", source = "helpApp", qualifiedByName = ["helpAppId"])
    )
    override fun toDto(s: StaticPage): StaticPageDTO

    @Named("helpAppId")
    @BeanMapping(ignoreByDefault = true)

    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoHelpAppId(helpApp: HelpApp): HelpAppDTO
}

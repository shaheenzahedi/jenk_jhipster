package org.aydm.danak.service.criteria

import org.aydm.danak.domain.enumeration.StaticPageStatus
import org.springdoc.api.annotations.ParameterObject
import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.Filter
import tech.jhipster.service.filter.StringFilter
import tech.jhipster.service.filter.UUIDFilter
import java.io.Serializable

/**
 * Criteria class for the [org.aydm.danak.domain.StaticPage] entity. This class is used in
 * [org.aydm.danak.web.rest.StaticPageResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/static-pages?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
data class StaticPageCriteria(
    var id: UUIDFilter? = null,
    var name: StringFilter? = null,
    var status: StaticPageStatusFilter? = null,
    var fileId: UUIDFilter? = null,
    var helpAppId: UUIDFilter? = null,
    var distinct: Boolean? = null
) : Serializable, Criteria {

    constructor(other: StaticPageCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.status?.copy(),
            other.fileId?.copy(),
            other.helpAppId?.copy(),
            other.distinct
        )

    /**
     * Class for filtering StaticPageStatus
     */
    class StaticPageStatusFilter : Filter<StaticPageStatus> {
        constructor()

        constructor(filter: StaticPageStatusFilter) : super(filter)

        override fun copy() = StaticPageStatusFilter(this)
    }

    override fun copy() = StaticPageCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

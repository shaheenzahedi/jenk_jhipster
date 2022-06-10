package org.aydm.danak.service.criteria

import org.springdoc.api.annotations.ParameterObject
import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.UUIDFilter
import java.io.Serializable

/**
 * Criteria class for the [org.aydm.danak.domain.HelpApp] entity. This class is used in
 * [org.aydm.danak.web.rest.HelpAppResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/help-apps?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
data class HelpAppCriteria(
    var id: UUIDFilter? = null,
    var staticPageId: UUIDFilter? = null,
    var staticPageIdId: UUIDFilter? = null,
    var distinct: Boolean? = null
) : Serializable, Criteria {

    constructor(other: HelpAppCriteria) :
        this(
            other.id?.copy(),
            other.staticPageId?.copy(),
            other.staticPageIdId?.copy(),
            other.distinct
        )

    override fun copy() = HelpAppCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

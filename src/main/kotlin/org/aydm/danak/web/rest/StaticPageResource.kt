package org.aydm.danak.web.rest

import org.aydm.danak.repository.StaticPageRepository
import org.aydm.danak.service.StaticPageQueryService
import org.aydm.danak.service.StaticPageService
import org.aydm.danak.service.criteria.StaticPageCriteria
import org.aydm.danak.service.dto.StaticPageDTO
import org.aydm.danak.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "staticPage"
/**
 * REST controller for managing [org.aydm.danak.domain.StaticPage].
 */
@RestController
@RequestMapping("/api")
class StaticPageResource(
    private val staticPageService: StaticPageService,
    private val staticPageRepository: StaticPageRepository,
    private val staticPageQueryService: StaticPageQueryService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "staticPage"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /static-pages` : Create a new staticPage.
     *
     * @param staticPageDTO the staticPageDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new staticPageDTO, or with status `400 (Bad Request)` if the staticPage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/static-pages")
    fun createStaticPage(@Valid @RequestBody staticPageDTO: StaticPageDTO): ResponseEntity<StaticPageDTO> {
        log.debug("REST request to save StaticPage : $staticPageDTO")
        if (staticPageDTO.id != null) {
            throw BadRequestAlertException(
                "A new staticPage cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = staticPageService.save(staticPageDTO)
        return ResponseEntity.created(URI("/api/static-pages/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /static-pages/:id} : Updates an existing staticPage.
     *
     * @param id the id of the staticPageDTO to save.
     * @param staticPageDTO the staticPageDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated staticPageDTO,
     * or with status `400 (Bad Request)` if the staticPageDTO is not valid,
     * or with status `500 (Internal Server Error)` if the staticPageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/static-pages/{id}")
    fun updateStaticPage(
        @PathVariable(value = "id", required = false) id: UUID,
        @Valid @RequestBody staticPageDTO: StaticPageDTO
    ): ResponseEntity<StaticPageDTO> {
        log.debug("REST request to update StaticPage : {}, {}", id, staticPageDTO)
        if (staticPageDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, staticPageDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!staticPageRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = staticPageService.update(staticPageDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    staticPageDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /static-pages/:id} : Partial updates given fields of an existing staticPage, field will ignore if it is null
     *
     * @param id the id of the staticPageDTO to save.
     * @param staticPageDTO the staticPageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staticPageDTO,
     * or with status {@code 400 (Bad Request)} if the staticPageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the staticPageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the staticPageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/static-pages/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateStaticPage(
        @PathVariable(value = "id", required = false) id: UUID,
        @NotNull @RequestBody staticPageDTO: StaticPageDTO
    ): ResponseEntity<StaticPageDTO> {
        log.debug("REST request to partial update StaticPage partially : {}, {}", id, staticPageDTO)
        if (staticPageDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, staticPageDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!staticPageRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = staticPageService.partialUpdate(staticPageDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, staticPageDTO.id.toString())
        )
    }

    /**
     * `GET  /static-pages` : get all the staticPages.
     *

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of staticPages in body.
     */
    @GetMapping("/static-pages") fun getAllStaticPages(
        criteria: StaticPageCriteria
    ): ResponseEntity<MutableList<StaticPageDTO>> {
        log.debug("REST request to get StaticPages by criteria: $criteria")
        val entityList = staticPageQueryService.findByCriteria(criteria)
        return ResponseEntity.ok().body(entityList)
    }

    /**
     * `GET  /static-pages/count}` : count all the staticPages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/static-pages/count")
    fun countStaticPages(criteria: StaticPageCriteria): ResponseEntity<Long> {
        log.debug("REST request to count StaticPages by criteria: $criteria")
        return ResponseEntity.ok().body(staticPageQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /static-pages/:id` : get the "id" staticPage.
     *
     * @param id the id of the staticPageDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the staticPageDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/static-pages/{id}")
    fun getStaticPage(@PathVariable id: UUID): ResponseEntity<StaticPageDTO> {
        log.debug("REST request to get StaticPage : $id")
        val staticPageDTO = staticPageService.findOne(id)
        return ResponseUtil.wrapOrNotFound(staticPageDTO)
    }
    /**
     *  `DELETE  /static-pages/:id` : delete the "id" staticPage.
     *
     * @param id the id of the staticPageDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/static-pages/{id}")
    fun deleteStaticPage(@PathVariable id: UUID): ResponseEntity<Void> {
        log.debug("REST request to delete StaticPage : $id")

        staticPageService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/static-pages?query=:query` : search for the staticPage corresponding
     * to the query.
     *
     * @param query the query of the staticPage search.
     * @return the result of the search.
     */
    @GetMapping("/_search/static-pages")
    fun searchStaticPages(@RequestParam query: String): MutableList<StaticPageDTO> {
        log.debug("REST request to search StaticPages for query $query")
        return staticPageService.search(query)
    }
}

package org.aydm.danak.web.rest

import org.aydm.danak.repository.HelpAppRepository
import org.aydm.danak.service.HelpAppQueryService
import org.aydm.danak.service.HelpAppService
import org.aydm.danak.service.criteria.HelpAppCriteria
import org.aydm.danak.service.dto.HelpAppDTO
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

private const val ENTITY_NAME = "helpApp"
/**
 * REST controller for managing [org.aydm.danak.domain.HelpApp].
 */
@RestController
@RequestMapping("/api")
class HelpAppResource(
    private val helpAppService: HelpAppService,
    private val helpAppRepository: HelpAppRepository,
    private val helpAppQueryService: HelpAppQueryService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "helpApp"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /help-apps` : Create a new helpApp.
     *
     * @param helpAppDTO the helpAppDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new helpAppDTO, or with status `400 (Bad Request)` if the helpApp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/help-apps")
    fun createHelpApp(@RequestBody helpAppDTO: HelpAppDTO): ResponseEntity<HelpAppDTO> {
        log.debug("REST request to save HelpApp : $helpAppDTO")
        if (helpAppDTO.id != null) {
            throw BadRequestAlertException(
                "A new helpApp cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = helpAppService.save(helpAppDTO)
        return ResponseEntity.created(URI("/api/help-apps/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /help-apps/:id} : Updates an existing helpApp.
     *
     * @param id the id of the helpAppDTO to save.
     * @param helpAppDTO the helpAppDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated helpAppDTO,
     * or with status `400 (Bad Request)` if the helpAppDTO is not valid,
     * or with status `500 (Internal Server Error)` if the helpAppDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/help-apps/{id}")
    fun updateHelpApp(
        @PathVariable(value = "id", required = false) id: UUID,
        @RequestBody helpAppDTO: HelpAppDTO
    ): ResponseEntity<HelpAppDTO> {
        log.debug("REST request to update HelpApp : {}, {}", id, helpAppDTO)
        if (helpAppDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, helpAppDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!helpAppRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = helpAppService.update(helpAppDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    helpAppDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /help-apps/:id} : Partial updates given fields of an existing helpApp, field will ignore if it is null
     *
     * @param id the id of the helpAppDTO to save.
     * @param helpAppDTO the helpAppDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated helpAppDTO,
     * or with status {@code 400 (Bad Request)} if the helpAppDTO is not valid,
     * or with status {@code 404 (Not Found)} if the helpAppDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the helpAppDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/help-apps/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateHelpApp(
        @PathVariable(value = "id", required = false) id: UUID,
        @RequestBody helpAppDTO: HelpAppDTO
    ): ResponseEntity<HelpAppDTO> {
        log.debug("REST request to partial update HelpApp partially : {}, {}", id, helpAppDTO)
        if (helpAppDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, helpAppDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!helpAppRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = helpAppService.partialUpdate(helpAppDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, helpAppDTO.id.toString())
        )
    }

    /**
     * `GET  /help-apps` : get all the helpApps.
     *

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of helpApps in body.
     */
    @GetMapping("/help-apps") fun getAllHelpApps(
        criteria: HelpAppCriteria
    ): ResponseEntity<MutableList<HelpAppDTO>> {
        log.debug("REST request to get HelpApps by criteria: $criteria")
        val entityList = helpAppQueryService.findByCriteria(criteria)
        return ResponseEntity.ok().body(entityList)
    }

    /**
     * `GET  /help-apps/count}` : count all the helpApps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/help-apps/count")
    fun countHelpApps(criteria: HelpAppCriteria): ResponseEntity<Long> {
        log.debug("REST request to count HelpApps by criteria: $criteria")
        return ResponseEntity.ok().body(helpAppQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /help-apps/:id` : get the "id" helpApp.
     *
     * @param id the id of the helpAppDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the helpAppDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/help-apps/{id}")
    fun getHelpApp(@PathVariable id: UUID): ResponseEntity<HelpAppDTO> {
        log.debug("REST request to get HelpApp : $id")
        val helpAppDTO = helpAppService.findOne(id)
        return ResponseUtil.wrapOrNotFound(helpAppDTO)
    }
    /**
     *  `DELETE  /help-apps/:id` : delete the "id" helpApp.
     *
     * @param id the id of the helpAppDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/help-apps/{id}")
    fun deleteHelpApp(@PathVariable id: UUID): ResponseEntity<Void> {
        log.debug("REST request to delete HelpApp : $id")

        helpAppService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/help-apps?query=:query` : search for the helpApp corresponding
     * to the query.
     *
     * @param query the query of the helpApp search.
     * @return the result of the search.
     */
    @GetMapping("/_search/help-apps")
    fun searchHelpApps(@RequestParam query: String): MutableList<HelpAppDTO> {
        log.debug("REST request to search HelpApps for query $query")
        return helpAppService.search(query)
    }
}

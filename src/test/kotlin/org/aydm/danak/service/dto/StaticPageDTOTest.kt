package org.aydm.danak.service.dto

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.web.rest.equalsVerifier
import org.junit.jupiter.api.Test
import java.util.UUID

class StaticPageDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(StaticPageDTO::class)
        val staticPageDTO1 = StaticPageDTO()
        staticPageDTO1.id = UUID.randomUUID()
        val staticPageDTO2 = StaticPageDTO()
        assertThat(staticPageDTO1).isNotEqualTo(staticPageDTO2)
        staticPageDTO2.id = staticPageDTO1.id
        assertThat(staticPageDTO1).isEqualTo(staticPageDTO2)
        staticPageDTO2.id = UUID.randomUUID()
        assertThat(staticPageDTO1).isNotEqualTo(staticPageDTO2)
        staticPageDTO1.id = null
        assertThat(staticPageDTO1).isNotEqualTo(staticPageDTO2)
    }
}

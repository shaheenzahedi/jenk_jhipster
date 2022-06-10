package org.aydm.danak.service.dto

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.web.rest.equalsVerifier
import org.junit.jupiter.api.Test
import java.util.UUID

class HelpAppDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(HelpAppDTO::class)
        val helpAppDTO1 = HelpAppDTO()
        helpAppDTO1.id = UUID.randomUUID()
        val helpAppDTO2 = HelpAppDTO()
        assertThat(helpAppDTO1).isNotEqualTo(helpAppDTO2)
        helpAppDTO2.id = helpAppDTO1.id
        assertThat(helpAppDTO1).isEqualTo(helpAppDTO2)
        helpAppDTO2.id = UUID.randomUUID()
        assertThat(helpAppDTO1).isNotEqualTo(helpAppDTO2)
        helpAppDTO1.id = null
        assertThat(helpAppDTO1).isNotEqualTo(helpAppDTO2)
    }
}

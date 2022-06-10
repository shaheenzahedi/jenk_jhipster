package org.aydm.danak.domain

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.web.rest.equalsVerifier
import org.junit.jupiter.api.Test
import java.util.UUID

class StaticPageTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(StaticPage::class)
        val staticPage1 = StaticPage()
        staticPage1.id = UUID.randomUUID()
        val staticPage2 = StaticPage()
        staticPage2.id = staticPage1.id
        assertThat(staticPage1).isEqualTo(staticPage2)
        staticPage2.id = UUID.randomUUID()
        assertThat(staticPage1).isNotEqualTo(staticPage2)
        staticPage1.id = null
        assertThat(staticPage1).isNotEqualTo(staticPage2)
    }
}

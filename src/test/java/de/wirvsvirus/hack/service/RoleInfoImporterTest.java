package de.wirvsvirus.hack.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import de.wirvsvirus.hack.model.Role;
import de.wirvsvirus.hack.service.impl.RoleInfoImporterServiceImpl;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {RoleInfoImporterServiceImpl.class})
public class RoleInfoImporterTest {

    @Autowired
    private RoleBasedTextSuggestionsService roleDataImporter;

    @Test
    public void role_text_csvImportTest() throws IOException {
        final List<String> forMeList = this.roleDataImporter.forOthers(Role.ARBEITNEHMER);
        assertThat(forMeList.get(0), is("Trink ruhig erst einmal ein Glas Wasser oder schnappe kurz frische Luft!"));

        final List<String> forOthers = this.roleDataImporter.forMe(Role.ARBEITNEHMER);
        assertThat(forOthers.get(0), is("Du schaffst es, gut Ding will Weile haben!"));
    }
}

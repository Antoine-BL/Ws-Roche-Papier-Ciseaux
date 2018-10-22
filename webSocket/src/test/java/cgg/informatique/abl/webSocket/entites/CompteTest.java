package cgg.informatique.abl.webSocket.entites;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompteTest {
    private static final Long ANOTHER_ID = 2L;
    private static final Long AN_ID = 1L;
    private static final String ANOTHER_PASSWORD = "pass2";
    private static final String A_PASSWORD = "pass1";
    private static final String AN_ALIAS = "alias1";
    private static final String ANOTHER_ALIAS = "alias2";
    private static final String AN_EMAIL = "email1";
    private static final String ANOTHER_EMAIL = "email2";
    private static final Role A_ROLE = Role.SENSEI;
    private static final Role ANOTHER_ROLE = Role.ANCIEN;
    private static final Role DEFAULT_ROLE = Role.NOUVEAU;
    private static final Groupe A_GROUP = Groupe.JAUNE;
    private static final Groupe ANOTHER_GROUP = Groupe.BLEU;
    private static final Groupe DEFAULT_GROUP = Groupe.BLANC;
    private static final String AN_AVATAR = "avatar1";
    private static final String ANOTHER_AVATAR = "avatar2";

    private Compte.Builder compteBuilder;

    @Before
    public void TestInit() {
        compteBuilder = initBuilder();
    }

    private Compte.Builder initBuilder (String email, String password) {
        return Compte.Builder()
                .avecCourriel(email)
                .avecMotDePasse(password);
    }

    private Compte.Builder initBuilder () {
        return initBuilder(AN_EMAIL, A_PASSWORD);
    }

    private Compte.Builder initBuilderWithID (Long id) {
        return initBuilder(AN_EMAIL, A_PASSWORD);
    }

    @Test
    public void compteClassShouldHaveBuilder() {
        Compte.CourrielBuilder builder = Compte.Builder();

        Assert.assertNotNull(builder);
    }

    @Test
    public void givenAnId_WhenBuild_ThenIdIsSet() {
        Long anId = AN_ID; //arrange

        Compte.Builder builder = initBuilderWithID(anId);//act

        Assert.assertEquals(anId, builder.build().getId());//assert
    }

    @Test
    public void givenAnotherId_WhenBuild_ThenIdIsSet() {
        Long anotherId = ANOTHER_ID;

        Compte.Builder builder = initBuilderWithID(anotherId);

        Assert.assertEquals(anotherId, builder.build().getId());
    }

    @Test
    public void givenAPassword_WhenBuild_ThenPasswordIsSet() {
        String aPassword = A_PASSWORD;

        compteBuilder.avecMotDePasse(aPassword);

        Assert.assertEquals(aPassword, compteBuilder.build().getMotPasse());
    }

    @Test
    public void givenAnotherPassword_WhenBuild_ThenPasswordIsSet() {
        String anotherPassword = ANOTHER_PASSWORD;

        compteBuilder.avecMotDePasse(anotherPassword);

        Assert.assertEquals(anotherPassword, compteBuilder.build().getMotPasse());
    }

    @Test
    public void givenAnAlias_WhenBuild_ThenAliasIsSet() {
        String anAlias = AN_ALIAS;

        compteBuilder.avecAlias(anAlias);

        Assert.assertEquals(anAlias, compteBuilder.build().getAlias());
    }

    @Test
    public void givenAnotherAlias_WhenBuild_ThenAliasIsSet() {
        String anotherAlias = ANOTHER_ALIAS;

        compteBuilder.avecAlias(anotherAlias);

        Assert.assertEquals(anotherAlias, compteBuilder.build().getAlias());
    }

    @Test
    public void givenAnEmail_WhenBuild_ThenEmailIsSet() {
        String anEmail = AN_EMAIL;

        compteBuilder.avecCourriel(anEmail);

        Assert.assertEquals(anEmail, compteBuilder.build().getCourriel());
    }

    @Test
    public void givenAnotherEmail_WhenBuild_ThenEmailIsSet() {
        String anEmail = ANOTHER_EMAIL;

        compteBuilder.avecCourriel(anEmail);

        Assert.assertEquals(anEmail, compteBuilder.build().getCourriel());
    }

    @Test
    public void givenARole_WhenBuild_ThenRoleIsSet() {
        Role aRole = A_ROLE;

        compteBuilder.avecRole(aRole);

        Assert.assertEquals(aRole, compteBuilder.build().getRole());
    }

    @Test
    public void givenAnotherRole_WhenBuild_ThenRoleIsSet() {
        Role anotherRole = ANOTHER_ROLE;

        compteBuilder.avecRole(anotherRole);

        Assert.assertEquals(anotherRole, compteBuilder.build().getRole());
    }

    @Test
    public void givenAGroup_WhenBuild_ThenRoleIsSet() {
        Groupe aGroup = A_GROUP;

        compteBuilder.avecGroupe(aGroup);

        Assert.assertEquals(aGroup, compteBuilder.build().getGroupe());
    }

    @Test
    public void givenAnotherGroup_WhenBuild_ThenGroupIsSet() {
        Groupe anotherGroup = ANOTHER_GROUP;

        compteBuilder.avecGroupe(anotherGroup);

        Assert.assertEquals(anotherGroup, compteBuilder.build().getGroupe());
    }

    @Test
    public void givenAnAvatar_WhenBuild_ThenAliasIsSet() {
        String anAvatar = AN_AVATAR;

        compteBuilder.avecAvatar(anAvatar);

        Assert.assertEquals(anAvatar, compteBuilder.build().getAvatar());
    }

    @Test
    public void givenAnotherAvatar_WhenBuild_ThenAliasIsSet() {
        String anotherAvatar = ANOTHER_AVATAR;

        compteBuilder.avecAvatar(anotherAvatar);

        Assert.assertEquals(anotherAvatar, compteBuilder.build().getAvatar());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenEmailIsNull_ThenExceptionIsThrown() {
        compteBuilder
                .avecCourriel(null)
                .avecMotDePasse(A_PASSWORD)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenPasswordIsNull_ThenExceptionIsThrown() {
        compteBuilder
                .avecMotDePasse(null)
                .build();
    }

    @Test
    public void whenNoGroupIsGiven_ThenGroupIsDefault() {
        Assert.assertEquals(DEFAULT_GROUP, compteBuilder.build().getGroupe());
    }

    @Test
    public void whenNoRoleIsGiven_ThenGroupIsDefault() {
        Assert.assertEquals(DEFAULT_ROLE, compteBuilder.build().getRole());
    }

    @Test
    public void compteHasProtectedDefaultConstructor() {
        new CompteChild();
        Assert.assertTrue(true);
    }
}

class CompteChild extends Compte{
    CompteChild() {
        super();
    }
}

package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompteControleurTest {
    @MockBean
    private CompteDao A_DAO;

    private static final Long AN_ID = 10L;
    private static final Long ANOTHER_ID = 15L;
    private static final String AN_EMAIL = "email";
    private static final String A_PASSWORD = "email";
    private static final Compte A_COMPTE = mock(Compte.class);
    private static final Compte ANOTHER_COMPTE = mock(Compte.class);
    private static final String BASE_URL = "http://localhost/test/%s";

    public CompteController COMPTE_CONTROLLER;

    @BeforeClass
    public static void classInit() {
        when(A_COMPTE.getId()).thenReturn(AN_ID);
        when(ANOTHER_COMPTE.getId()).thenReturn(ANOTHER_ID);
    }

    @Before
    public void testInit() {
        A_DAO = mock(CompteDao.class);

        HttpServletRequest mockRequest = new MockHttpServletRequest(null, null, "/test");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        COMPTE_CONTROLLER = new CompteController(A_DAO);
    }

    @After
    public void teardown() {
        RequestContextHolder.resetRequestAttributes();
        clearInvocations(A_DAO);
    }

    @Test
    public void givenTheIDOfACompte_WhenGetCompte_ThenReturnsACompte() {
        Compte aCompte = A_COMPTE;
        aDaoGetCompteSetup(A_COMPTE, A_COMPTE.getId());

        ResponseEntity<Compte> compteRetourne = COMPTE_CONTROLLER.getCompte(aCompte.getId());

        Assert.assertSame(aCompte, compteRetourne.getBody());
    }

    @Test
    public void givenTheIdOfAnotherCompte_WhenGetCompte_ThenReturnsAnotherCompte() {
        Compte anotherCompte = ANOTHER_COMPTE;
        aDaoGetCompteSetup(ANOTHER_COMPTE, ANOTHER_COMPTE.getId());

        ResponseEntity<Compte> compteRetourne = COMPTE_CONTROLLER.getCompte(anotherCompte.getId());

        Assert.assertSame(anotherCompte, compteRetourne.getBody());
    }

    @Test
    public void givenTheIdOfANonExistentCompte_WhenGetCompte_ThenBadRequestIsThrown() {
        Long idOfANonExistentCompte = 100L;
        aDaoGetCompteSetup(null, 100L);

        ResponseEntity<Compte> compteRetourne = COMPTE_CONTROLLER.getCompte(idOfANonExistentCompte);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, compteRetourne.getStatusCode());
    }

    @Test
    public void WhenGetAllCompte_ThenReturnsAllComptes() {
        CompteDao aDao = A_DAO;
        List<Compte> allComptes = new ArrayList<>();
        allComptes.add(A_COMPTE);
        allComptes.add(ANOTHER_COMPTE);
        when(aDao.findAll()).thenReturn(allComptes);

        List<Compte> comptesRetournes = COMPTE_CONTROLLER.getAllCompte();

        Assert.assertSame(comptesRetournes, allComptes);
    }

    @Test
    public void GivenACompte_WhenAddCompte_ThenCompteIsAddedAndCreatedIsReturned() {
        testCompteCreated(A_COMPTE);
    }

    @Test
    public void GivenAnotherCompte_WhenAddCompte_ThenCompteIsAddedAndCreatedIsReturned() {
        testCompteCreated(ANOTHER_COMPTE);
    }

    @Test
    public void GivenACompteThatExists_WhenAddCompte_ThenBadRequestIsReturned() {
        testCompteExists(A_COMPTE);
    }

    @Test
    public void GivenAnotherCompteThatExists_WhenAddCompte_ThenBadRequestIsReturned() {
        testCompteExists(ANOTHER_COMPTE);
    }

    @Test
    public void GivenACompteWithAnEmailThatExists_WhenAddCompte_ThenBadRequestIsReturned() {
        testEmailExists(A_COMPTE);
    }

    @Test
    public void GivenAnotherCompteWithAnEmailThatExists_WhenAddCompte_ThenBadRequestIsReturned() {
        testEmailExists(ANOTHER_COMPTE);
    }

    @Test
    public void GivenACompteThatExists_WhenDeleteCompte_ThenIsDeletedAndOkIsReturned() {
        Compte aCompteThatExists = compteThatExists(A_COMPTE);

        ResponseEntity response = COMPTE_CONTROLLER.deleteCompte(aCompteThatExists.getId());

        verify(A_DAO).deleteById(aCompteThatExists.getId());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void GivenAnotherCompteThatExists_WhenDeleteCompte_ThenIsDeletedAndOkIsReturned() {
        Compte anotherCompteThatExists = compteThatExists(ANOTHER_COMPTE);

        ResponseEntity response = COMPTE_CONTROLLER.deleteCompte(anotherCompteThatExists.getId());

        verify(A_DAO).deleteById(anotherCompteThatExists.getId());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void GivenAnotherCompteThatDoesntExists_WhenDeleteCompte_ThenBadRequestIsReturned() {
        Compte anotherCompteThatExists = A_COMPTE;

        ResponseEntity response = COMPTE_CONTROLLER.deleteCompte(anotherCompteThatExists.getId());

        Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private void testEmailExists(Compte compteThatExists) {
        when(A_DAO.existsByCourriel(compteThatExists.getCourriel())).thenReturn(true);

        ResponseEntity response = COMPTE_CONTROLLER.addCompte(compteThatExists);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void testCompteExists(Compte compteThatExists) {
        when(A_DAO.existsById(compteThatExists.getId())).thenReturn(true);

        ResponseEntity response = COMPTE_CONTROLLER.addCompte(compteThatExists);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void testCompteCreated(Compte aCompte) {
        when(A_DAO.save(aCompte)).thenReturn(aCompte);

        ResponseEntity response = COMPTE_CONTROLLER.addCompte(aCompte);

        verify(A_DAO).save(aCompte);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertEquals(String.format(BASE_URL, aCompte.getId()), response.getHeaders().getLocation().toString());
    }

    private void aDaoGetCompteSetup(Compte returnCompte, Long forId) {
        Optional<Compte> returnValue = returnCompte == null ? Optional.empty() : Optional.of(returnCompte);

        when(
                A_DAO.findById(forId)
        ).thenReturn(
                returnValue
        );
    }

    private Compte compteThatExists(Compte compte) {
        when(A_DAO.existsById(compte.getId())).thenReturn(true);

        return compte;
    }
}
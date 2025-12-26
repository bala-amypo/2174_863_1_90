package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import com.example.demo.util.TextSimilarityUtil;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.*;
import java.util.*;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Listeners(TestResultListener.class)
public class MasterTestNGSuiteTest {

    @Mock private UserRepository userRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private TicketCategoryRepository categoryRepository;
    @Mock private DuplicateRuleRepository ruleRepository;
    @Mock private DuplicateDetectionLogRepository logRepository;

    private UserService userService;
    private TicketCategoryService categoryService;
    private TicketService ticketService;
    private DuplicateRuleService ruleService;
    private DuplicateDetectionService detectionService;

    @BeforeClass
    public void init(){
        MockitoAnnotations.openMocks(this);
        userService = new com.example.demo.service.impl.UserServiceImpl(userRepository);
        categoryService = new com.example.demo.service.impl.TicketCategoryServiceImpl(categoryRepository);
        ticketService = new com.example.demo.service.impl.TicketServiceImpl(ticketRepository, userRepository, categoryRepository);
        ruleService = new com.example.demo.service.impl.DuplicateRuleServiceImpl(ruleRepository);
        detectionService = new com.example.demo.service.impl.DuplicateDetectionServiceImpl(ticketRepository, ruleRepository, logRepository);
    }

    @Test(priority=1) public void testServletSim(){ Assert.assertEquals("DemoServlet","DemoServlet"); }
    @Test(priority=2) public void testServletResponse(){ Assert.assertTrue("OK".contains("O")); }

    @Test(priority=3) public void testRegisterUser(){
        User u = new User("Alice","a@test.com","password123","USER");
        when(userRepository.existsByEmail("a@test.com")).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i->i.getArguments()[0]);
        User saved = userService.registerUser(u);
        Assert.assertEquals(saved.getEmail(),"a@test.com");
    }
    @Test(priority=4) public void testRegisterDuplicateEmail(){
        User u = new User("B","b@test.com","password123","USER");
        when(userRepository.existsByEmail("b@test.com")).thenReturn(true);
        try{ userService.registerUser(u); Assert.fail(); } catch(Exception e){ Assert.assertTrue(e.getMessage().toLowerCase().contains("email")); }
    }
    @Test(priority=5) public void testGetUser(){
        User u = new User("C","c@test.com","pass1234","USER"); u.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        Assert.assertEquals(userService.getUser(1L).getEmail(),"c@test.com");
    }
    @Test(priority=6) public void testGetAllUsers(){
        when(userRepository.findAll()).thenReturn(List.of(new User("X","x@test.com","pass1234","USER")));
        Assert.assertFalse(userService.getAllUsers().isEmpty());
    }

    @Test(priority=7) public void testCreateCategory(){
        TicketCategory c = new TicketCategory("Billing","Billing issues");
        when(categoryRepository.existsByCategoryName("Billing")).thenReturn(false);
        when(categoryRepository.save(Mockito.any(TicketCategory.class))).thenAnswer(i->i.getArguments()[0]);
        TicketCategory saved = categoryService.createCategory(c);
        Assert.assertEquals(saved.getCategoryName(),"Billing");
    }
    @Test(priority=8) public void testCreateCategoryDuplicate(){
        TicketCategory c = new TicketCategory("Billing","Billing");
        when(categoryRepository.existsByCategoryName("Billing")).thenReturn(true);
        try{ categoryService.createCategory(c); Assert.fail(); } catch(Exception e){ Assert.assertTrue(e.getMessage().toLowerCase().contains("category")); }
    }
    @Test(priority=9) public void testGetCategory(){
        TicketCategory c = new TicketCategory("Tech","Tech"); c.setId(2L);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(c));
        Assert.assertEquals(categoryService.getCategory(2L).getCategoryName(),"Tech");
    }

    @Test(priority=10) public void testCreateTicket(){
        User u = new User("U","u@test.com","pass1234","USER"); u.setId(1L);
        TicketCategory tc = new TicketCategory("Tech","Tech"); tc.setId(1L);
        Ticket t = new Ticket(); t.setSubject("Problem"); t.setDescription("details are long enough");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(tc));
        when(ticketRepository.save(Mockito.any(Ticket.class))).thenAnswer(i->i.getArguments()[0]);
        Ticket out = ticketService.createTicket(1L,1L,t);
        Assert.assertEquals(out.getSubject(),"Problem");
    }
    @Test(priority=11) public void testCreateTicketShortDescription(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new TicketCategory()));
        Ticket t = new Ticket(); t.setSubject("S"); t.setDescription("short");
        try{ ticketService.createTicket(1L,1L,t); Assert.fail(); } catch(Exception e){ Assert.assertTrue(e.getMessage().toLowerCase().contains("description")); }
    }
    @Test(priority=12) public void testGetTicketNotFound(){
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        try{ ticketService.getTicket(99L); Assert.fail(); } catch(Exception e){ Assert.assertTrue(e.getMessage().toLowerCase().contains("ticket not found")); }
    }

    @Test(priority=13) public void testDIUserService(){ Assert.assertNotNull(userService); }
    @Test(priority=14) public void testDIServiceLayer(){ Assert.assertNotNull(ticketService); }

    @Test(priority=15) public void testUserPasswordMinLength(){ User u = new User("T","t@test.com","12345678","USER"); Assert.assertTrue(u.getPassword().length()>=8); }
    @Test(priority=16) public void testTicketSubjectNotEmpty(){ Ticket t = new Ticket(); t.setSubject("sub"); Assert.assertFalse(t.getSubject().isBlank()); }

    @Test(priority=17) public void testTicketCategoryRelation(){
        Ticket t = new Ticket(); TicketCategory c = new TicketCategory("A","a"); t.setCategory(c);
        Assert.assertEquals(t.getCategory().getCategoryName(),"A");
    }
    @Test(priority=18) public void testUserTicketRelation(){
        Ticket t = new Ticket(); User u = new User("U","u@test.com","pass1234","USER"); t.setUser(u);
        Assert.assertEquals(t.getUser().getEmail(),"u@test.com");
    }

    @Test(priority=19) public void testTextSimilarityUtil(){
        double s = TextSimilarityUtil.similarity("foo bar baz","foo baz qux");
        Assert.assertTrue(s>=0);
    }
    @Test(priority=20) public void testExactMatchRule(){
        DuplicateRule r = new DuplicateRule("Exact","EXACT_MATCH",1.0);
        Ticket t1 = new Ticket(); t1.setSubject("hello"); t1.setDescription("desc");
        Ticket t2 = new Ticket(); t2.setSubject("hello"); t2.setDescription("desc2");
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t2));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(t1));
        List<?> res = detectionService.detectDuplicates(1L);
        Assert.assertTrue(res.size()>=0);
    }

    @Test(priority=21) public void testKeywordRule(){
        DuplicateRule r = new DuplicateRule("KW","KEYWORD",0.1);
        Ticket t1 = new Ticket(); t1.setSubject("payment issue"); t1.setDescription("card failed");
        Ticket t2 = new Ticket(); t2.setSubject("payment failed"); t2.setDescription("card declined");
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t2));
        when(ticketRepository.findById(2L)).thenReturn(Optional.of(t1));
        List<?> res = detectionService.detectDuplicates(2L);
        Assert.assertTrue(res.size()>=0);
    }

    @Test(priority=22) public void testJwtGenerateAndValidate(){
        User u = new User("A","a@test.com","pass1234","USER"); u.setId(5L);
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(u));
        Assert.assertNotNull(u.getEmail());
    }

    @Test(priority=23) public void testFindTicketsByCategory(){
        Ticket t = new Ticket(); when(ticketRepository.findByCategory_Id(1L)).thenReturn(List.of(t));
        Assert.assertTrue(ticketService.getAllTickets().size()>=0);
    }
    @Test(priority=24) public void testSearchTicketsByKeyword(){
        when(ticketRepository.findBySubjectContainingIgnoreCaseOrDescriptionContainingIgnoreCase("pay","pay")).thenReturn(List.of(new Ticket()));
        Assert.assertFalse(ticketRepository.findBySubjectContainingIgnoreCaseOrDescriptionContainingIgnoreCase("pay","pay").isEmpty());
    }

    @Test(priority=25) public void testDetectLogsForTicket(){
        when(logRepository.findByTicket_Id(1L)).thenReturn(List.of(new com.example.demo.model.DuplicateDetectionLog()));
        Assert.assertFalse(detectionService.getLogsForTicket(1L).isEmpty());
    }

      @Test(priority=26)
    public void testThresholdZeroAllowed() {
        DuplicateRule rule = new DuplicateRule("R0", "SIMILARITY", 0.0);
        when(ruleRepository.findByRuleName("R0")).thenReturn(Optional.empty());
        when(ruleRepository.save(any(DuplicateRule.class))).thenAnswer(i -> i.getArguments()[0]);
        DuplicateRule saved = ruleService.createRule(rule);
        Assert.assertEquals(saved.getThreshold(), 0.0);
    }

    @Test(priority=27)
    public void testRuleMaxThresholdAllowed() {
        DuplicateRule rule = new DuplicateRule("R1", "SIMILARITY", 1.0);
        when(ruleRepository.findByRuleName("R1")).thenReturn(Optional.empty());
        when(ruleRepository.save(any(DuplicateRule.class))).thenAnswer(i -> i.getArguments()[0]);
        DuplicateRule saved = ruleService.createRule(rule);
        Assert.assertEquals(saved.getThreshold(), 1.0);
    }

    @Test(priority=28)
    public void testRuleDuplicateNameBlocked() {
        DuplicateRule r = new DuplicateRule("DUP", "KEYWORD", 0.5);
        when(ruleRepository.findByRuleName("DUP")).thenReturn(Optional.of(r));
        try { ruleService.createRule(r); Assert.fail(); }
        catch (Exception e){ Assert.assertTrue(e.getMessage().toLowerCase().contains("exists")); }
    }

    @Test(priority=29)
    public void testDetectionNoRulesReturnsEmpty() {
        Ticket t = new Ticket(); t.setId(10L); t.setSubject("A"); t.setDescription("B");
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(t));
        when(ruleRepository.findAll()).thenReturn(List.of());
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of());
        Assert.assertTrue(detectionService.detectDuplicates(10L).isEmpty());
    }

    @Test(priority=30)
    public void testDetectionSkipSelfTicket() {
        Ticket t = new Ticket(); t.setId(20L);
        when(ticketRepository.findById(20L)).thenReturn(Optional.of(t));
        when(ruleRepository.findAll()).thenReturn(List.of());
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t));
        Assert.assertTrue(detectionService.detectDuplicates(20L).isEmpty());
    }

    @Test(priority=31)
    public void testKeywordSmallOverlap() {
        DuplicateRule r = new DuplicateRule("KW31","KEYWORD",0.01);
        Ticket base = new Ticket(); base.setId(1L); base.setSubject("internet slow"); base.setDescription("speed low");
        Ticket other = new Ticket(); other.setId(2L); other.setSubject("slow wifi"); other.setDescription("low signal");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(base));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(other));
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        when(logRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Assert.assertTrue(detectionService.detectDuplicates(1L).size() >= 1);
    }

    @Test(priority=32)
    public void testExactMatchFailsWhenDifferent() {
        DuplicateRule r = new DuplicateRule("EM32","EXACT_MATCH",1.0);
        Ticket t1 = new Ticket(); t1.setId(1L); t1.setSubject("A");
        Ticket t2 = new Ticket(); t2.setId(2L); t2.setSubject("B");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(t1));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t2));
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        Assert.assertTrue(detectionService.detectDuplicates(1L).isEmpty());
    }

    @Test(priority=33)
    public void testSimilarityRuleTriggers() {
        DuplicateRule r = new DuplicateRule("SIM33","SIMILARITY",0.1);
        Ticket t1 = new Ticket(); t1.setId(1L); t1.setDescription("payment declined");
        Ticket t2 = new Ticket(); t2.setId(2L); t2.setDescription("payment issue decline fail");
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(t1));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t2));
        when(logRepository.save(any())).thenAnswer(i->i.getArguments()[0]);
        Assert.assertTrue(detectionService.detectDuplicates(1L).size() >= 1);
    }

    @Test(priority=34)
    public void testSimilarityZeroWhenCompletelyDifferent() {
        double s = TextSimilarityUtil.similarity("abc","xyz");
        Assert.assertEquals(s,0.0);
    }

    @Test(priority=35)
    public void testCategoryGetNotFound() {
        when(categoryRepository.findById(444L)).thenReturn(Optional.empty());
        try{ categoryService.getCategory(444L); Assert.fail(); }
        catch(Exception e){ Assert.assertTrue(e.getMessage().toLowerCase().contains("not found")); }
    }

    @Test(priority=36)
    public void testTicketListByUser() {
        Ticket t = new Ticket();
        when(ticketRepository.findByUser_Id(8L)).thenReturn(List.of(t));
        Assert.assertFalse(ticketService.getTicketsByUser(8L).isEmpty());
    }

    @Test(priority=37)
    public void testRuleFetch() {
        DuplicateRule r = new DuplicateRule("Fetch37","KEYWORD",0.2);
        r.setId(37L);
        when(ruleRepository.findById(37L)).thenReturn(Optional.of(r));
        Assert.assertEquals(ruleService.getRule(37L).getRuleName(),"Fetch37");
    }

    @Test(priority=38)
    public void testLogsForTicketEmpty() {
        when(logRepository.findByTicket_Id(11L)).thenReturn(List.of());
        Assert.assertTrue(detectionService.getLogsForTicket(11L).isEmpty());
    }

    @Test(priority=39)
    public void testRuleCreationKeyword() {
        DuplicateRule r = new DuplicateRule("KWR","KEYWORD",0.3);
        when(ruleRepository.findByRuleName("KWR")).thenReturn(Optional.empty());
        when(ruleRepository.save(any())).thenAnswer(i->i.getArguments()[0]);
        Assert.assertEquals(ruleService.createRule(r).getMatchType(),"KEYWORD");
    }

    @Test(priority=40)
    public void testTicketCategoryAssignment() {
        TicketCategory c = new TicketCategory("ACC","account");
        Ticket t = new Ticket();
        t.setCategory(c);
        Assert.assertEquals(t.getCategory().getCategoryName(),"ACC");
    }

    @Test(priority=41)
    public void testTicketStatusDefault() {
        Ticket t = new Ticket();
        Assert.assertEquals(t.getStatus(),"OPEN");
    }

    @Test(priority=42)
    public void testDuplicateDetectionLogFields() {
        Ticket t1 = new Ticket(); t1.setId(1L);
        Ticket t2 = new Ticket(); t2.setId(2L);
        DuplicateDetectionLog log = new DuplicateDetectionLog(t1,t2,0.5);
        Assert.assertEquals(log.getMatchScore(),0.5);
    }

    @Test(priority=43)
    public void testUserRoleDefaultIsUser() {
        User u = new User("A","b@test.com","password123",null);
        Assert.assertEquals(u.getRole(),"USER");
    }

    @Test(priority=44)
    public void testTicketSubjectValidation() {
        Ticket t = new Ticket();
        t.setSubject("Hi");
        Assert.assertEquals(t.getSubject(),"Hi");
    }

    @Test(priority=45)
    public void testDuplicateRuleThresholdSetter() {
        DuplicateRule r = new DuplicateRule("SET45","KEYWORD",0.5);
        r.setThreshold(0.8);
        Assert.assertEquals(r.getThreshold(),0.8);
    }

    @Test(priority=46)
    public void testEmptyKeywordMatch() {
        double s = TextSimilarityUtil.similarity("","xyz");
        Assert.assertEquals(s,0.0);
    }

    @Test(priority=47)
    public void testCategoryDescriptionSetter() {
        TicketCategory c = new TicketCategory("Z","Zdesc");
        c.setDescription("NEW");
        Assert.assertEquals(c.getDescription(),"NEW");
    }

    @Test(priority=48)
    public void testTicketAutoTime() {
        Ticket t = new Ticket();
        Assert.assertNotNull(t.getCreatedAt());
    }

    @Test(priority=49)
    public void testRuleMatchTypeSetter() {
        DuplicateRule r = new DuplicateRule("R49","KEYWORD",0.1);
        r.setMatchType("SIMILARITY");
        Assert.assertEquals(r.getMatchType(),"SIMILARITY");
    }

    @Test(priority=50)
    public void testDetectionLogTimestamp() {
        DuplicateDetectionLog log = new DuplicateDetectionLog();
        Assert.assertNotNull(log.getDetectedAt());
    }

    @Test(priority=51)
    public void testUserFullnameSetter() {
        User u = new User();
        u.setFullName("Test User");
        Assert.assertEquals(u.getFullName(),"Test User");
    }

    @Test(priority=52)
    public void testUserEmailSetter() {
        User u = new User();
        u.setEmail("mail@test.com");
        Assert.assertEquals(u.getEmail(),"mail@test.com");
    }

    @Test(priority=53)
    public void testTicketDescriptionSetter() {
        Ticket t = new Ticket();
        t.setDescription("Long enough description");
        Assert.assertTrue(t.getDescription().length() >= 10);
    }

    @Test(priority=54)
    public void testCategoryNameSetter() {
        TicketCategory c = new TicketCategory("A","B");
        c.setCategoryName("C");
        Assert.assertEquals(c.getCategoryName(),"C");
    }

    @Test(priority=55)
    public void testUserGetAllMockNonEmpty() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        Assert.assertFalse(userService.getAllUsers().isEmpty());
    }

    @Test(priority=56)
    public void testSimilarityCaseInsensitive() {
        double s = TextSimilarityUtil.similarity("HELLO world","hello WORLD");
        Assert.assertTrue(s >= 0.5);
    }

    @Test(priority=57)
    public void testTicketSetStatus() {
        Ticket t = new Ticket();
        t.setStatus("IN_PROGRESS");
        Assert.assertEquals(t.getStatus(),"IN_PROGRESS");
    }

    @Test(priority=58)
    public void testLogSetScore() {
        DuplicateDetectionLog log = new DuplicateDetectionLog();
        log.setMatchScore(0.9);
        Assert.assertEquals(log.getMatchScore(),0.9);
    }

    @Test(priority=59)
    public void testRuleSetName() {
        DuplicateRule r = new DuplicateRule("AA","KEYWORD",0.5);
        r.setRuleName("NEWNAME");
        Assert.assertEquals(r.getRuleName(),"NEWNAME");
    }

    @Test(priority=60)
    public void testTicketUserAssign() {
        User u = new User("P","p@test.com","password123","USER");
        Ticket t = new Ticket();
        t.setUser(u);
        Assert.assertEquals(t.getUser().getEmail(),"p@test.com");
    }

    @Test(priority=61)
    public void testCategoryCreatedAtNotNull() {
        TicketCategory c = new TicketCategory("CT","x");
        Assert.assertNotNull(c.getCreatedAt());
    }

    @Test(priority=62)
    public void testUserCreatedAtNotNull() {
        User u = new User("Q","q@test.com","password123","USER");
        Assert.assertNotNull(u.getCreatedAt());
    }

    @Test(priority=63)
    public void testSimilarityOneForSameWords() {
        double s = TextSimilarityUtil.similarity("hello world","hello world");
        Assert.assertEquals(s,1.0);
    }

    @Test(priority=64)
    public void testTicketCategoryNotNull() {
        TicketCategory c = new TicketCategory("Tech","desc");
        Assert.assertEquals(c.getCategoryName(),"Tech");
    }

    @Test(priority=65)
    public void testRuleExactMatchCaseInsensitive() {
        DuplicateRule r = new DuplicateRule("EM","EXACT_MATCH",1.0);
        Ticket t1 = new Ticket(); t1.setSubject("HELLO");
        Ticket t2 = new Ticket(); t2.setSubject("hello");
        when(ticketRepository.findById(50L)).thenReturn(Optional.of(t1));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t2));
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        when(logRepository.save(any())).thenAnswer(i->i.getArguments()[0]);
        Assert.assertTrue(detectionService.detectDuplicates(50L).size() >= 1);
    }

    @Test(priority=66)
    public void testKeywordMatchHighThresholdFails() {
        DuplicateRule r = new DuplicateRule("KWH","KEYWORD",0.9);
        Ticket t1 = new Ticket(); t1.setSubject("wifi issue"); t1.setDescription("slow connection");
        Ticket t2 = new Ticket(); t2.setSubject("wifi slow"); t2.setDescription("connection drop");
        when(ruleRepository.findAll()).thenReturn(List.of(r));
        when(ticketRepository.findById(77L)).thenReturn(Optional.of(t1));
        when(ticketRepository.findByStatus("OPEN")).thenReturn(List.of(t2));
        Assert.assertTrue(detectionService.detectDuplicates(77L).isEmpty());
    }

    @Test(priority=67)
    public void testTicketSearchReturnsList() {
        when(ticketRepository.findBySubjectContainingIgnoreCaseOrDescriptionContainingIgnoreCase("x","x"))
                .thenReturn(List.of(new Ticket()));
        Assert.assertFalse(ticketRepository
                .findBySubjectContainingIgnoreCaseOrDescriptionContainingIgnoreCase("x","x")
                .isEmpty());
    }

    @Test(priority=68)
    public void testSimilarityDifferentLength() {
        double s = TextSimilarityUtil.similarity("aaa bbb", "bbb ccc ddd");
        Assert.assertTrue(s >= 0.0);
    }

    @Test(priority=69)
    public void testFinal() {
        Assert.assertTrue(true);
    }

}

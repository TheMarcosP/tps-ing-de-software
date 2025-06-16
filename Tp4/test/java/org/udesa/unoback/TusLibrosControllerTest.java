//package org.udesa.tuslibros.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.udesa.tuslibros.model.Clock;
//import org.udesa.tuslibros.model.MerchantApi;
//import org.udesa.tuslibros.model.TusLibrosSystemFacade;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class TusLibrosControllerTest {
//    @Autowired MockMvc mockMvc;
//    @Autowired TusLibrosSystemFacade systemFacade;
//    @MockBean Clock clock;
//    @MockBean MerchantApi merchantApi;
//
//    @BeforeEach public void beforeEach() {
//        systemFacade.reset();
//        when( clock.now() ).then( it -> LocalDateTime.now() );
//        when( clock.today() ).then( it -> LocalDate.now() );
//    }
//
//    @Test public void test01CanCreateCartWithValidUserAndPassword() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//        assertTrue( listCartIdentifiedAs( cartId ).isEmpty() );
//    }
//
//    @Test public void test02CanNotCreateCartWithInvalidUser() throws Exception {
//        createCartForFailing( "Ringo", "Rpass" );
//    }
//
//    @Test public void test03CanNotCreateCartWithInvalidPassword() throws Exception {
//        createCartForFailing( "Jhon", "Rpass" );
//    }
//
//    @Test public void test04CanAddItemsToACreatedCart() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//        addItem( cartId, "bookA", 2 );
//
//        assertEquals( 2, listCartIdentifiedAs( cartId ).get( "bookA" ) );
//    }
//
//    @Test public void test05CanNotAddItemToNotCreatedCart() throws Exception {
//        addItemFailing( 100, "bookA", 2 );
//    }
//
//    @Test public void test06CanNotAddItemNotSellByTheStore() throws Exception {
//        addItemFailing( createCartFor( "Jhon", "Jpass" ), "bookX", 2 );
//    }
//
//    @Test public void test08CanNotListCartOfInvalidCartId() throws Exception {
//        listCartIdentifiedAsFailing( 100 );
//    }
//
//    @Test public void test09ListCartReturnsTheRightNumberOfItems() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//
//        addItem( cartId, "bookA", 2 );
//        addItem( cartId, "bookB", 1 );
//
//        var content = listCartIdentifiedAs( cartId );
//
//        assertEquals( 2, content.get( "bookA" ) );
//        assertEquals( 1, content.get( "bookB" ) );
//        assertEquals( 2, content.size() );
//    }
//
//    @Test public void test10CheckOutReturnsTransactionIdAndImpactsCustomerPurchases() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//        addItem( cartId, "bookA", 2 );
//
//        when( merchantApi.debitFrom( any(), any() ) ).thenAnswer( it ->  42 );
//        var txId = checkOutCart( cartId, "1234567890123456", "Jhon", "202510" );
//
//        //	self assert: merchantProcessorTransactionId equals: currentTransactionId.
//        //	self assertValidUserBougthOnlyItemSellByTheStore
//
//    }
//
//    @Test public void test10_1_CanNotCheckoutAnAlreadyCheckedOutCart() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//        addItem( cartId, "bookA", 2 );
//
//        when( merchantApi.debitFrom( any(), any() ) ).thenAnswer( it ->  42 );
//        checkOutCart( cartId, "1234567890123456", "Jhon", "202510" );
//
//        checkOutCartFailing( cartId, "1234567890123456", "Jhon", "202510" );
//        //	self assertValidUserBougthOnlyItemSellByTheStore
//    }
//
//    @Test public void test11CanNotCheckoutANotCreatedCart() throws Exception {
//        checkOutCartFailing( 10, "1234567890123456", "Jhon", "202510" );
//    }
//
//    @Test public void test12CanNotCheckoutAnEmptyCart() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//
//        checkOutCartFailing( cartId, "1234567890123456", "Jhon", "202510" );
//    }
//
//    @Test public void test13CanNotCheckoutWithAnExpiredCreditCard() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//        addItem( cartId, "bookA", 2 );
//
//        checkOutCartFailing( cartId, "1234567890123456", "Jhon", "202410" );
//        //			self assertThereAreNoPurchasesForValidUser ]
//    }
//
//    @Test public void test14ListPurchasesIncludesBoughtItems() throws Exception {
//        var cartId = createCartFor( "Jhon", "Jpass" );
//
//        addItem( cartId, "bookA", 2 );
//        addItem( cartId, "bookB", 1 );
//
//        when( merchantApi.debitFrom( any(), any() ) ).thenAnswer( it ->  42 );
//        checkOutCart( cartId, "1234567890123456", "Jhon", "202510" );
//
//        List<Map> purchases = purchasesOf( "Jhon", "Jpass" );
//        assertEquals( 2, purchases.size() );
//        assertEquals( "bookB", purchases.getFirst().get("item") );
//        assertEquals( 1, purchases.getFirst().get( "quantity") );
//        assertEquals( "bookA", purchases.getLast().get("item") );
//        assertEquals( 2, purchases.getLast().get( "quantity") );
//    }
//
//    @Test public void test15CanNotListPurchasesOfInvalidCustomer() throws Exception {
//        purchasesOfFailing( "Ringo", "Rpass" );
//    }
//
//    @Test public void test16CanNotListPurchasesOfValidCustomerWithInvalidPassword() throws Exception {
//        purchasesOfFailing( "Jhon", "Rpass" );
//    }
//
//    @Test public void test17CanNotAddToCartWhenSessionIsExpired() throws Exception {
//        int cartId = createCartFor( "Jhon", "Jpass" );
//
//        when( clock.now() ).then( it -> LocalDateTime.now().plusMinutes( 31 ) );
//        addItemFailing( cartId, "bookA", 2 );
//    }
//
//    @Test public void test18CanNotListCartWhenSessionIsExpired() throws Exception {
//        int cartId = createCartFor( "Jhon", "Jpass" );
//
//        when( clock.now() ).then( it -> LocalDateTime.now().plusMinutes( 31 ) );
//        listCartIdentifiedAsFailing( cartId );
//    }
//
//    @Test public void test19CanNotCheckOutCartWhenSessionIsExpired() throws Exception {
//        int cartId = createCartFor( "Jhon", "Jpass" );
//        addItem( cartId, "bookA", 2 );
//
//        when( clock.now() ).then( it -> LocalDateTime.now().plusMinutes( 31 ) );
//        checkOutCartFailing( cartId, "1234567890123456", "Jhon", "202510" );
//        //			self assertThereAreNoPurchasesForValidUser ].
//    }
//
//    public int createCartFor( String user, String pass ) throws Exception {
//        return Integer.parseInt( mockMvc.perform( post( "/createCartFor" )
//                                                          .contentType( MediaType.APPLICATION_JSON )
//                                                          .param( "user", user )
//                                                          .param( "pass", pass ) )
//                                         .andDo( print() )
//                                         .andExpect( status().is( 200 ) )
//                                         .andReturn()
//                                         .getResponse()
//                                         .getContentAsString() );
//    }
//
//    public void createCartForFailing( String user, String pass ) throws Exception {
//        mockMvc.perform( post( "/createCartFor" )
//                                 .contentType( MediaType.APPLICATION_JSON )
//                                 .param( "user", user )
//                                 .param( "pass", pass ) )
//                .andDo( print() )
//                .andExpect( status().is( 500 ) );
//    }
//
//    public Map listCartIdentifiedAs( int cartId ) throws Exception {
//        return new ObjectMapper().readValue( mockMvc.perform( get( "/listCartIdentifiedAs/" + cartId ) )
//                                                     .andDo( print() )
//                                                     .andExpect( status().is( 200 ) )
//                                                     .andReturn()
//                                                     .getResponse()
//                                                     .getContentAsString(), HashMap.class );
//    }
//
//    private void listCartIdentifiedAsFailing( int cartId ) throws Exception {
//        mockMvc.perform( get( "/listCartIdentifiedAs/" + cartId ) )
//                .andDo( print() )
//                .andExpect( status().is( 500 ) );
//    }
//
//
//    private int checkOutCart( int cartId, String number, String owner, String month ) throws Exception {
//        return Integer.parseInt( mockMvc.perform( post( "/checkOutCart/" + cartId )
//                                                          .param( "cardNumber", number )
//                                                          .param( "owner", owner )
//                                                          .param( "month", month ) )
//                                         .andDo( print() )
//                                         .andExpect( status().is( 200 ) )
//                                         .andReturn()
//                                         .getResponse()
//                                         .getContentAsString() );
//    }
//
//    private void checkOutCartFailing( int cartId, String number, String owner, String month ) throws Exception {
//        mockMvc.perform( post( "/checkOutCart/" + cartId )
//                                 .param( "cardNumber", number )
//                                 .param( "owner", owner )
//                                 .param( "month", month ) )
//                .andDo( print() )
//                .andExpect( status().is( 500 ) );
//    }
//
//    private void addItem( int cartId, String item, int quantity ) throws Exception {
//        mockMvc.perform( post( "/addItem/" + cartId )
//                                 .contentType( MediaType.APPLICATION_JSON )
//                                 .param( "item", item )
//                                 .param( "quantity", "" + quantity ) )
//                .andDo( print() )
//                .andExpect( status().is( 200 ) );
//    }
//
//    private void addItemFailing( int cartId, String item, int quantity ) throws Exception {
//        mockMvc.perform( post( "/addItem/" + cartId )
//                                 .contentType( MediaType.APPLICATION_JSON )
//                                 .param( "item", item )
//                                 .param( "quantity", "" + quantity ) )
//                .andDo( print() )
//                .andExpect( status().is( 500 ) );
//    }
//
//    private List<Map> purchasesOf( String user, String pass ) throws Exception {
//        return new ObjectMapper().readValue( mockMvc.perform( get( "/purchasesOf" )
//                                                                      .param( "user", user )
//                                                                      .param( "pass", pass ) )
//                                                     .andDo( print() )
//                                                     .andExpect( status().is( 200 ) )
//                                                     .andReturn()
//                                                     .getResponse()
//                                                     .getContentAsString(), List.class );
//
//    }
//
//    private void purchasesOfFailing( String user, String pass ) throws Exception {
//        mockMvc.perform( get( "/purchasesOf" )
//                                 .param( "user", user )
//                                 .param( "pass", pass ) )
//                .andDo( print() )
//                .andExpect( status().is( 500 ) );
//    }
//}

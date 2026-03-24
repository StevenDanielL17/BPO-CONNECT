package com.bpoconnect.controller;

import org.springframework.stereotype.Controller;
@Controller public class ScreenPopController { private static ScreenPopController instance = new ScreenPopController(); private ScreenPopController(){} public static ScreenPopController getInstance(){return instance;} public Object fetchCustomerProfile(String ani){return null;} public void createNewCustomerProfile(){} }

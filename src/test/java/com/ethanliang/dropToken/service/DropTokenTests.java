package com.ethanliang.dropToken.service;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringRunner.class)
@SpringBootTest

@WebAppConfiguration
public class DropTokenTests {

    @Before
    public void init() {
        System.out.println("test start");
    }

    @After
    public void after(){
        System.out.println("test end");
    }
}

package org.roag.camel;

import org.apache.camel.spring.Main;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main=new Main();
        main.setApplicationContext((AbstractApplicationContext) ServiceLocator.getContext());
        main.run(args);

    }

}


package org.roag.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by eurohlam on 23.10.16.
 */
@Component("rssPolicy")
public class RssRoutePolicy extends RoutePolicySupport {
    private final Logger logger = LoggerFactory.getLogger(RssRoutePolicy.class);


    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        super.onExchangeBegin(route, exchange);
        logger.debug("Policy: exchange started for route = {}, exchange = {}", route.getId(), exchange.getExchangeId());
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        super.onExchangeDone(route, exchange);
        logger.debug("Policy: exchange finished for route = {}, exchange = {}", route.getId(), exchange.getExchangeId());
        try {
            //we want to stop route as soon as all new feeds has been polled into a file
            stopRoute(route);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onStop(Route route) {
        super.onStop(route);
        logger.debug("Policy: stop route = {}", route.getId());
        try {
            route.getRouteContext().getCamelContext().removeRoute(route.getId());
            logger.debug("Policy: route = {} has been removed from context", route.getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}

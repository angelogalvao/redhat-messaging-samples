package com.angelogalvao.samples.artemis;

import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.core.postoffice.RoutingStatus;
import org.apache.activemq.artemis.core.server.MessageReference;
import org.apache.activemq.artemis.core.server.RoutingContext;
import org.apache.activemq.artemis.core.server.ServerConsumer;
import org.apache.activemq.artemis.core.server.ServerSession;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.apache.activemq.artemis.core.transaction.Transaction;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * My custom broker plugin implementation. It only prints the message flowing in and out the broker.
 *
 * @author <a href="mailto:angelogalvao@gmail.com">Ângelo Galvão</a>
 */
public class MyBrokerPlugin implements ActiveMQServerPlugin {

    private static final Logger log = Logger.getLogger(MyBrokerPlugin.class);

    // In Java 9 this is significantly simpler
    private Set<String> blackList = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("activemq.", "notif")));

    @Override
    public void afterMessageRoute(Message message, RoutingContext context, boolean direct, boolean rejectDuplicates, RoutingStatus result) {

        // skip the messages that it is in black list, like activemq.notifications
        if (isFromBlacklistAddress(message)) return;

        message.putBooleanProperty("_MyCustomBrokerPlugin_MessageWasRouted", true);

    }

    @Override
    public void beforeSend(ServerSession session, Transaction tx, Message message, boolean direct, boolean noAutoCreateQueue) {
        if (isFromBlacklistAddress(message)) return;

        // it's a new message arriving to broker cluster
        if(message.getMessageID() == 0)
            message.putBooleanProperty("_MyCustomBrokerPlugin_isNewMessage", true);
        else
            message.putBooleanProperty("_MyCustomBrokerPlugin_isNewMessage", false);

    }



    @Override
    public void afterSend(ServerSession session, Transaction tx, Message message, boolean direct, boolean noAutoCreateQueue, RoutingStatus result) {

        // skip the messages that it is in black list, like activemq.notifications
        if (isFromBlacklistAddress(message)) return;

        log.info(String.format("MyBrokerPlugin.afterSend: Message getting IN of the broker. Message: %s", message));

        // delaying the route message for consistency logging.
        if( message.getBooleanProperty("_MyCustomBrokerPlugin_MessageWasRouted") &&  message.getBooleanProperty("_MyCustomBrokerPlugin_isNewMessage")    )
            log.info(String.format("MyBrokerPlugin.afterSend: Message getting OUT of the broker. Message: %s", message));

    }

    @Override
    public void afterDeliver(ServerConsumer consumer, MessageReference reference) {

        // skip the messages that it is in black list, like activemq.notifications
        if (isFromBlacklistAddress(reference.getMessage())) return;

        log.info(String.format("MyBrokerPlugin.afterDeliver: Message getting OUT of the broker. Message: %s", reference.getMessage()));

    }

    /**
     * Test if message address is in blacklist.
     * @param message the message
     * @return true if it is in the blacklist or false if not
     */
    private boolean isFromBlacklistAddress(Message message) {
        // skip the messages that it is in black list, like activemq.notifications
        if( blackList.stream().anyMatch( address -> message.getAddress().startsWith(address) ) )
            return true;
        return false;
    }
}

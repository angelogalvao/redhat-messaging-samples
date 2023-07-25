
This configuration will not work on AMQ 7.10 and 7.11:

<address name="TEST.TOPIC">
            <multicast>
                <queue name="mqtt.TEST.TOPIC"/>
            </multicast>
</address>


This configration will work in AMQ 7.9 and bellow:

<address name="TEST.TOPIC">
            <multicast>
                <queue name="mqtt.TEST/TOPIC"/>
            </multicast>
</address>

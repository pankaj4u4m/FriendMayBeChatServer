package com.metly.openfire.bussiness;

import org.apache.log4j.Logger;
import org.xmpp.packet.IQ;

import com.metly.openfire.dao.IQDB;

public class IQProcessor {
    private static final Logger log = Logger.getLogger(IQProcessor.class);

    private IQDB iqdb;

    public IQProcessor() {
        iqdb = new IQDB();
    }

    public void process(IQ packet) {
        this.iqdb.save(packet);
    }

}

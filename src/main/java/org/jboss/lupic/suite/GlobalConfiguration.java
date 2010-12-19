package org.jboss.lupic.suite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.jboss.lupic.parser.listener.ParserListener;
import org.jboss.lupic.suite.utils.Instantiator;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "GlobalConfiguration", propOrder = { "listeners", "patternRetriever", "maskRetriever",
    "sampleRetriever" })
@XmlRootElement(name = "global-configuration")
public class GlobalConfiguration extends Configuration {

    protected List<Listener> listeners;
    protected PatternRetriever patternRetriever;
    protected MaskRetriever maskRetriever;
    protected SampleRetriever sampleRetriever;

    /*
     * accessors
     */
    @XmlElement(name = "listener")
    public List<Listener> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<Listener>();
        }
        return this.listeners;
    }

    @XmlElement(name = "pattern-retriever")
    public PatternRetriever getPatternRetriever() {
        return patternRetriever;
    }

    public void setPatternRetriever(PatternRetriever value) {
        this.patternRetriever = value;
    }

    @XmlElement(name = "mask-retriever")
    public MaskRetriever getMaskRetriever() {
        return maskRetriever;
    }

    public void setMaskRetriever(MaskRetriever value) {
        this.maskRetriever = value;
    }

    @XmlElement(name = "sample-retriever")
    public SampleRetriever getSampleRetriever() {
        return sampleRetriever;
    }

    public void setSampleRetriever(SampleRetriever value) {
        this.sampleRetriever = value;
    }

    /*
     * logic
     */
    @XmlTransient
    private Map<String, ParserListener> parserListeners = new HashMap<String, ParserListener>();

    public Collection<ParserListener> getConfiguredListeners() {
        for (Listener listener : listeners) {
            final String type = listener.getType();
            if (!parserListeners.containsKey(type)) {
                ParserListener parserListener = new Instantiator<ParserListener>().getInstance(type);
                parserListener.setProperties(listener);
                parserListeners.put(type, parserListener);
            }
        }
        return parserListeners.values();
    }
}

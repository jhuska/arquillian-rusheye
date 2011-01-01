package org.jboss.rusheye.suite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "Perception", propOrder = { "onePixelTreshold", "globalDifferenceTreshold", "globalDifferenceAmount" })
public class Perception {

    public static final Number NOT_THIS_TYPE = new Double("0");

    protected Integer onePixelTreshold;
    protected Integer globalDifferenceTreshold;
    protected String globalDifferenceAmount;

    @XmlElement(name = "one-pixel-treshold")
    public Integer getOnePixelTreshold() {
        return onePixelTreshold;
    }

    public void setOnePixelTreshold(Integer value) {
        this.onePixelTreshold = value;
    }

    @XmlElement(name = "global-difference-treshold")
    public Integer getGlobalDifferenceTreshold() {
        return globalDifferenceTreshold;
    }

    public void setGlobalDifferenceTreshold(Integer value) {
        this.globalDifferenceTreshold = value;
    }

    @XmlElement(name = "global-difference-pixel-amount")
    public String getGlobalDifferenceAmount() {
        return globalDifferenceAmount;
    }

    public void setGlobalDifferenceAmount(String value) {
        this.globalDifferenceAmount = value;
    }

    /*
     * 
     */
    @XmlTransient
    public Long getGlobalDifferencePixelAmount() {
        Number number = getGlobalDifferenceAmount(AmountType.PIXEL);
        return (number != NOT_THIS_TYPE) ? number.longValue() : null;
    }

    public void setGlobalDifferencePixelAmount(long globalDifferencePixelAmount) {
        this.globalDifferenceAmount = Long.toString(globalDifferencePixelAmount) + "px";
    }

    @XmlTransient
    public Short getGlobalDifferencePercentage() {
        Number number = getGlobalDifferenceAmount(AmountType.PERCENTAGE);
        return (number != NOT_THIS_TYPE) ? number.shortValue() : null;
    }

    public void setGlobalDifferencePercentage(short globalDifferencePercentage) {
        this.globalDifferenceAmount = Short.valueOf(globalDifferencePercentage) + "%";
    }

    public static enum AmountType {
        PERCENTAGE("([0-9]{1,2}|100)%"), PIXEL("^([0-9]+)px$");

        private Pattern pattern;

        private AmountType(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        Pattern getPattern() {
            return pattern;
        }

        Number parseAmount(String string) {
            if (this == PERCENTAGE) {
                return Short.valueOf(string);
            } else {
                return Long.valueOf(string);
            }
        }
    }

    public Number getGlobalDifferenceAmount(AmountType amountType) {
        String amount = getGlobalDifferenceAmount();
        if (amount == null) {
            return null;
        }

        Matcher matcher = amountType.getPattern().matcher(amount);
        if (matcher.lookingAt()) {
            return amountType.parseAmount(matcher.group(1));
        } else {
            return NOT_THIS_TYPE;
        }
    }
}
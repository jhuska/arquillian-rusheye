package org.jboss.lupic.suite;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.jboss.lupic.retriever.sample.SampleRetriever;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "Sample")
public class Sample {

    protected String source;

    @Resource
    @XmlTransient
    public SampleRetriever sampleRetriever;

    @XmlTransient
    FutureTask<BufferedImage> future = new FutureTask<BufferedImage>(new Callable<BufferedImage>() {
        public BufferedImage call() throws Exception {
            return sampleRetriever.retrieve(source, null);
        };
    });

    @XmlAttribute
    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    /*
     * logic
     */
    public void run() {
        future.run();
    }

    public BufferedImage get() throws ExecutionException, InterruptedException {
        return future.get();
    }

}
package cz.tisnik.ImageDiff;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlWriter
{
    private static final String XML_FILE_NAME = "results.xml";

    private BufferedWriter writer = null;

    public XmlWriter(File outputDirectory) throws IOException
    {
        this.writer = createXmlFile(outputDirectory);
        printXmlHeader();
    }

    private BufferedWriter createXmlFile(File outputDirectory) throws IOException
    {
        return outputDirectory == null ? null : new BufferedWriter(new FileWriter(new File(outputDirectory, XML_FILE_NAME)));
    }

    public void printXmlHeader() throws IOException
    {
        if (this.writer != null)
        {
            this.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            this.println("<visual-suite-result>");
        }
    }

    public void printXmlFooter() throws IOException
    {
        if (this.writer != null)
        {
            this.println("</visual-suite-result>");
        }
    }

    public void close() throws IOException
    {
        if (this.writer != null)
        {
            this.writer.close();
        }
    }

    private void print(String format, Object... o) throws IOException
    {
        this.writer.write(String.format(format, o));
    }

    private void println(String format, Object... o) throws IOException
    {
        print(format, o);
        this.writer.write("\n");
    }

    public void printResultForOneImage(Configuration configuration, String imageFileName, BufferedImage[] sourceImages,
            ComparisonResult comparisonResult) throws IOException
    {
        String dirName = imageFileName.substring(0, imageFileName.lastIndexOf('.'));

        printComparisonResult(comparisonResult, dirName);
        printPerception(comparisonResult);
        this.println("        </pattern>");
        this.println("    </test>");
        createAndFillResultDirectory(configuration, sourceImages, comparisonResult, dirName);
    }

    private void printComparisonResult(ComparisonResult comparisonResult, String dirName) throws IOException
    {
        boolean equalImages = comparisonResult.isEqualsImages();
        String result = equalImages ? "same" : "different";
        this.println("    <test id=\"%s\">", dirName);
        this.println("        <pattern id=\"%s\" result=\"%s\">", dirName, result);
        if (equalImages)
        {
            this.println("            <output/>");
        }
        else
        {
            this.println("            <output>%s</output>", dirName);
        }
    }

    private void printPerception(ComparisonResult cr) throws IOException
    {
        this.println("            <perception>");
        this.println("                <area width=\"%d\" height=\"%d\" />", cr.getAreaWidth(), cr.getAreaHeight());
        this.println("                <rectangle>");
        this.println("                    <vertex x=\"%d\" y=\"%d\" />", cr.getRectangleMin().x, cr.getRectangleMin().y);
        this.println("                    <vertex x=\"%d\" y=\"%d\" />", cr.getRectangleMax().x, cr.getRectangleMax().y);
        this.println("                </rectangle>");
        this.println("                <totalPixels>%d</totalPixels>", cr.getTotalPixels());
        this.println("                <maskedPixels>%d</maskedPixels>", cr.getMaskedPixels());
        this.println("                <perceptibleDifferentPixels>%d</perceptibleDifferentPixels>", cr.getPerceptibleDiffs());
        this.println("                <globalDifferentPixels>%d</globalDifferentPixels>", cr.getDifferentPixels());
        this.println("                <unperceptibleDifferentPixels>%d</unperceptibleDifferentPixels>", cr.getSmallDifferences());
        this.println("                <samePixels>%d</samePixels>", cr.getEqualPixels());
        this.println("            </perception>");
    }

    private void createAndFillResultDirectory(Configuration configuration, BufferedImage[] sourceImages,
            ComparisonResult comparisonResult, String dirName) throws IOException
    {
        File newDir = new File(configuration.getHtmlOutputDirectory(), dirName);
        Log.logProcess("creating directory %s", newDir.getAbsolutePath());
        newDir.mkdir();
        if (!comparisonResult.isEqualsImages())
        {
            Log.logProcess("writing source and diff images to directory %s", dirName);
            //ImageUtils.writeImage(sourceImages[0], newDir, "source1.png");
            //ImageUtils.writeImage(sourceImages[1], newDir, "source2.png");
            //ImageUtils.writeImage(comparisonResult.getDiffImage(), newDir, "diff.png");
        }
        else
        {
            Log.logProcess("writing source image to directory %s", dirName);
            //ImageUtils.writeImage(sourceImages[0], newDir, "source1.png");
        }
        createHtmlFile(newDir, comparisonResult);
    }

    private void createHtmlFile(File newDir, ComparisonResult cr) throws IOException
    {
        String templateName = cr.isEqualsImages() ? "template_same_images.html" : "template_different_images.html";
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(newDir, "result.html")));
        BufferedReader template = new BufferedReader(new FileReader(templateName));
        String line;
        while ((line = template.readLine()) != null)
        {
            writer.write(replacePlaceholders(cr, newDir, line));
        }
        template.close();
        writer.close();
    }

    private String replacePlaceholders(ComparisonResult cr, File newDir, String line)
    {
        StringBuffer out = new StringBuffer();
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find())
        {
            matcher.appendReplacement(out, getPlaceholderValue(cr, matcher.group(1)));
        }
        matcher.appendTail(out);
        return out.toString() + "\n";
    }

    private String getPlaceholderValue(ComparisonResult cr, String group)
    {
        Method method;
        try
        {
            // call appropriate getter
            method = cr.getClass().getDeclaredMethod("get" + group, (Class<?>[]) null);
            return "" + method.invoke(cr, (Object[]) null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return group;
    }

}

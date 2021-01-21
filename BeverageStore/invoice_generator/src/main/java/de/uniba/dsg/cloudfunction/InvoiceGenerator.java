package de.uniba.dsg.cloudfunction;

import com.lowagie.text.DocumentException;
import de.uniba.dsg.cloudfunction.models.Order;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InvoiceGenerator {
    private final Order ORDER;
    private final String TEMPLATE_NAME;
    private final ByteArrayOutputStream OUTPUT_STREAM;

    public InvoiceGenerator(Order order, String templateName) {
        ORDER = order;
        TEMPLATE_NAME = templateName;
        OUTPUT_STREAM = new ByteArrayOutputStream();
    }

    public byte[] generate() throws DocumentException, IOException {
        String html = parseThymeleafTemplate();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(OUTPUT_STREAM);

        return OUTPUT_STREAM.toByteArray();
    }

    public void dispose() throws IOException {
        OUTPUT_STREAM.close();
    }

    private String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("order", ORDER);

        return templateEngine.process(TEMPLATE_NAME, context);
    }
}

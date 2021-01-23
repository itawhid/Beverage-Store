package de.uniba.dsg.cloudfunction;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class InvoiceMailSenderFunction implements BackgroundFunction<GcsEvent> {

    private final String mailFrom = System.getenv("MAIL_FROM");
    private final String username = System.getenv("USERNAME");
    private final String password = System.getenv("PASSWORD");
    private final String smtpServerHost = System.getenv("SMTP_SERVER_HOST");
    private final String smtpServerPort = System.getenv("SMTP_SERVER_PORT");

    private static final Logger logger = Logger.getLogger(InvoiceMailSenderFunction.class.getName());

    @Override
    public void accept(GcsEvent event, Context context) throws IOException, MessagingException {
        Blob blob = getBlob(event.getBucket(), event.getName());

        String mailTo = blob.getMetadata().get("email");
        String orderNumber = blob.getMetadata().get("order_number");
        String customerName = blob.getMetadata().get("customer_name");

        List<File> attachments = Collections.singletonList(downloadFile(blob, event.getName()));

        try {
            sendMail(mailTo, orderNumber, customerName, attachments);

            logger.info("Invoice for  Order: " + orderNumber + " sent to - " + mailTo);
        } catch (IOException | MessagingException ex) {
            logger.info("Exception: " + ex.getMessage());

            throw ex;
        }
    }

    private void sendMail(String mailTo, String orderNumber, String customerName, List<File> attachments) throws IOException, MessagingException {
        String mailContentType = "text/html";
        String mailSubject = "Beverage Store : Order Invoice - " + orderNumber;
        String mailBodyText =
                "Hello " + customerName + ","
                + "<br/><br/>"
                + "Please find the invoice for your order (" + orderNumber +") attached to this E-Mail. Thank you for shopping with us."
                + "<br/><br/>"
                + "Regards,"
                + "<br/>"
                + "Beverage Store Team";


        MailSender mailSender = new MailSender(mailFrom, smtpServerHost, smtpServerPort, username, password);

        try {
            mailSender.send(mailTo, mailSubject, mailBodyText, mailContentType, attachments);
        } catch (IOException | MessagingException ex) {
            logger.info("Exception: " + ex.getMessage());

            throw ex;
        }
    }

    private Blob getBlob(String bucketName, String fileName) {
        Bucket bucket = StorageOptions.getDefaultInstance()
                .getService()
                .get(bucketName);

        return bucket.get(fileName);
    }

    private File downloadFile(Blob blob, String fileName) {
        String filePath = "/tmp/" + fileName;

        blob.downloadTo(Paths.get(filePath));

        return new File(filePath);
    }
}
